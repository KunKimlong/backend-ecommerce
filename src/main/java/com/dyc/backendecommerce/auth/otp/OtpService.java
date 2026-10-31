package com.dyc.backendecommerce.auth.otp;

import com.dyc.backendecommerce.user.User;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Issues and checks one-time codes for any {@link OtpPurpose} over any {@link OtpChannel}. The limits
 * here are what make a 6-digit code safe: it dies after a few wrong guesses or a few minutes, and
 * sending is throttled so nobody can flood an inbox (or, later, run up an SMS bill).
 */
@Service
@Transactional
public class OtpService {
  static final Duration VALID_FOR = Duration.ofMinutes(10);
  static final int MAX_ATTEMPTS = 5;
  static final Duration RESEND_COOLDOWN = Duration.ofSeconds(60);
  static final int MAX_PER_HOUR = 5;

  private static final SecureRandom RANDOM = new SecureRandom();

  private final Map<String, OtpChannel> channels;
  private final VerificationCodeRepository codeRepository;
  private final PasswordEncoder passwordEncoder;

  public OtpService(
      List<OtpChannel> channels,
      VerificationCodeRepository codeRepository,
      PasswordEncoder passwordEncoder) {
    this.channels =
        channels.stream().collect(Collectors.toUnmodifiableMap(OtpChannel::channel, Function.identity()));
    this.codeRepository = codeRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public boolean isAvailable(String channel) {
    OtpChannel c = channels.get(channel);
    return c != null && c.isEnabled();
  }

  /**
   * Sends a fresh code and retires any earlier unused one. Returns false without sending when the
   * user is inside the resend cooldown or over the hourly limit.
   */
  public boolean issue(User user, OtpPurpose purpose, String channel) {
    OtpChannel delivery = channels.get(channel);
    if (delivery == null || !delivery.isEnabled()) {
      throw new IllegalStateException("One-time code channel not available: " + channel);
    }
    LocalDateTime now = LocalDateTime.now();
    boolean coolingDown =
        codeRepository
            .findFirstByUserAndPurposeOrderByCreatedAtDesc(user, purpose)
            .map(last -> last.getCreatedAt().isAfter(now.minus(RESEND_COOLDOWN)))
            .orElse(false);
    if (coolingDown
        || codeRepository.countByUserAndPurposeAndCreatedAtAfter(user, purpose, now.minusHours(1))
            >= MAX_PER_HOUR) {
      return false;
    }

    // Only the newest code works, so an old email can't be used after a resend
    codeRepository.findByUserAndPurposeAndConsumedAtIsNull(user, purpose).forEach(old -> old.setConsumedAt(now));

    String code = "%06d".formatted(RANDOM.nextInt(1_000_000));
    codeRepository.save(
        VerificationCode.builder()
            .user(user)
            .purpose(purpose)
            .channel(channel)
            .codeHash(passwordEncoder.encode(code))
            .expiresAt(now.plus(VALID_FOR))
            .build());
    delivery.send(user, code, purpose, VALID_FOR);
    return true;
  }

  /**
   * True once per code: when it matches the user's newest unexpired, unused code for {@code purpose}.
   * Runs in its own transaction so a wrong guess is counted even though the caller then fails its
   * request (and rolls back its own transaction); otherwise the attempt limit would never bite.
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public boolean verify(User user, OtpPurpose purpose, String code) {
    return check(user, purpose, code, true);
  }

  /**
   * Like {@link #verify} but leaves a correct code usable, for flows that confirm the code in one step
   * and use it in a later one. Wrong guesses still count towards the same limit.
   */
  @Transactional(propagation = Propagation.REQUIRES_NEW)
  public boolean peek(User user, OtpPurpose purpose, String code) {
    return check(user, purpose, code, false);
  }

  private boolean check(User user, OtpPurpose purpose, String code, boolean consume) {
    LocalDateTime now = LocalDateTime.now();
    VerificationCode latest =
        codeRepository.findTopByUserAndPurposeOrderByCreatedAtDesc(user, purpose).orElse(null);
    if (latest == null
        || latest.getConsumedAt() != null
        || latest.getExpiresAt().isBefore(now)
        || latest.getAttempts() >= MAX_ATTEMPTS) {
      return false;
    }
    // Only wrong guesses count, so checking a code and then using it doesn't eat into the limit
    if (!passwordEncoder.matches(code, latest.getCodeHash())) {
      latest.setAttempts(latest.getAttempts() + 1);
      return false;
    }
    if (consume) {
      latest.setConsumedAt(now);
    }
    return true;
  }
}
