package com.dyc.backendecommerce.auth.password;

import com.dyc.backendecommerce.auth.otp.EmailOtpChannel;
import com.dyc.backendecommerce.auth.otp.OtpPurpose;
import com.dyc.backendecommerce.auth.otp.OtpService;
import com.dyc.backendecommerce.shared.exception.BadRequestException;
import com.dyc.backendecommerce.user.User;
import com.dyc.backendecommerce.user.UserRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * "Forgot password" for every account type (customers and staff): a code is emailed, and the code
 * plus a new password replaces the old one and signs out every existing session.
 */
@Service
@Transactional
@RequiredArgsConstructor
public class PasswordResetService {
  private static final String INVALID_CODE = "The code is invalid or has expired";

  private final UserRepository userRepository;
  private final OtpService otpService;
  private final PasswordEncoder passwordEncoder;

  /**
   * 202 whether or not the email belongs to an account (and whether or not a code went out because of
   * the resend limits), so the endpoint can't be used to find out who has an account. 503 when email
   * sending isn't configured, which says nothing about any account.
   */
  public ResponseEntity<Void> requestReset(ForgotPasswordRequest request) {
    if (!otpService.isAvailable(EmailOtpChannel.CHANNEL)) {
      return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
    }
    userRepository
        .findByEmail(request.getEmail())
        .filter(User::isActive)
        .ifPresent(user -> otpService.issue(user, OtpPurpose.PASSWORD_RESET, EmailOtpChannel.CHANNEL));
    return ResponseEntity.status(HttpStatus.ACCEPTED).build();
  }

  /**
   * Step 2 of the reset: confirms the code without using it up, so the page can move on to asking for
   * the new password. An email with no account fails here exactly like a wrong code.
   */
  public ResponseEntity<Void> verifyCode(VerifyResetCodeRequest request) {
    User user = activeUser(request.getEmail());
    if (user == null || !otpService.peek(user, OtpPurpose.PASSWORD_RESET, request.getCode())) {
      throw new BadRequestException(INVALID_CODE);
    }
    return ResponseEntity.noContent().build();
  }

  /** Step 3: the same code again (checked and used up this time) plus the new password. */
  public ResponseEntity<Void> resetPassword(ResetPasswordRequest request) {
    User user = activeUser(request.getEmail());
    if (user == null || !otpService.verify(user, OtpPurpose.PASSWORD_RESET, request.getCode())) {
      throw new BadRequestException(INVALID_CODE);
    }
    user.setPassword(passwordEncoder.encode(request.getNewPassword()));
    // The code arrived by email, so the address is proven too (this also rescues a pending sign-up)
    user.setEmailVerified(true);
    user.setCredentialsChangedAt(Instant.now().truncatedTo(ChronoUnit.SECONDS));
    return ResponseEntity.noContent().build();
  }

  private User activeUser(String email) {
    return userRepository.findByEmail(email).filter(User::isActive).orElse(null);
  }
}
