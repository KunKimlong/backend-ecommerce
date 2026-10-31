package com.dyc.backendecommerce.auth;

import com.dyc.backendecommerce.auth.otp.EmailOtpChannel;
import com.dyc.backendecommerce.auth.otp.OtpPurpose;
import com.dyc.backendecommerce.auth.otp.OtpService;
import com.dyc.backendecommerce.shared.entity.CustomUserDetails;
import com.dyc.backendecommerce.shared.enums.UserRole;
import com.dyc.backendecommerce.shared.exception.BadRequestException;
import com.dyc.backendecommerce.shared.util.JwtUtil;
import com.dyc.backendecommerce.user.User;
import com.dyc.backendecommerce.user.UserRepository;
import java.time.Duration;
import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {
  public static final String COOKIE_NAME = "authToken";
  public static final String REFRESH_COOKIE_NAME = "refreshToken";
  public static final String REMEMBER_COOKIE_NAME = "rememberMe";
  private static final String WAIT_FOR_CODE =
      "A code was sent a moment ago. Please wait a minute before trying again.";

  @Value("${spring.application.jwt.access-token-exp}")
  private int tokenExpiresIn;

  @Value("${spring.application.jwt.refresh-token-exp}")
  private int refreshTokenExpiresIn;

  @Value("${spring.application.cookie-secure}")
  private boolean cookieSecure;

  private final UserRepository userRepository;
  private final AuthenticationManager authenticationManager;
  private final JwtUtil jwtUtil;
  private final UserDetailsService userDetailsService;
  private final PasswordEncoder passwordEncoder;
  private final OtpService otpService;

  public ResponseEntity<AuthResponse> login(AuthRequest request) {
    User user = userRepository.findByEmail(request.getEmail()).orElse(null);
    if (user == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    try {
      authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
    } catch (AuthenticationException e) {
      // Same response as an unknown email, so callers can't tell which accounts exist
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    // Right password but email not confirmed yet: send a fresh code (within limits) and say so
    if (needsVerification(user)) {
      otpService.issue(user, OtpPurpose.EMAIL_VERIFICATION, EmailOtpChannel.CHANNEL);
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    return issueTokens(user, !Boolean.FALSE.equals(request.getRememberMe()), HttpStatus.OK);
  }

  // If email sending is switched off later, unverified customers can still sign in
  private boolean needsVerification(User user) {
    return !user.isEmailVerified() && otpService.isAvailable(EmailOtpChannel.CHANNEL);
  }

  /**
   * Signs {@code user} in: access + refresh tokens in the body and as cookies. Shared by every way
   * of proving who you are (password, registration, social sign-in) so they all end the same way.
   */
  public ResponseEntity<AuthResponse> issueTokens(User user, boolean remember, HttpStatus status) {
    UserDetails userDetails = userDetailsService.loadUserByUsername(user.getEmail());
    String accessToken = jwtUtil.generateAccessToken(userDetails, user);
    String refreshToken = jwtUtil.generateRefreshToken(userDetails);

    return ResponseEntity.status(status)
        .header(HttpHeaders.SET_COOKIE, buildAccessCookie(accessToken, remember).toString())
        .header(HttpHeaders.SET_COOKIE, buildRefreshCookie(refreshToken, remember).toString())
        .header(HttpHeaders.SET_COOKIE, buildRememberCookie(remember).toString())
        .body(new AuthResponse(accessToken, tokenExpiresIn, refreshToken, refreshTokenExpiresIn));
  }

  // A non-persistent cookie (no Max-Age) is dropped when the browser closes
  private ResponseCookie buildCookieWith(
      String name, String value, Duration maxAge, boolean persistent) {
    return ResponseCookie.from(name, value)
        .maxAge(persistent ? maxAge : Duration.ofSeconds(-1))
        .sameSite("Lax")
        .httpOnly(true)
        .secure(cookieSecure)
        .path("/")
        .build();
  }

  private ResponseCookie buildAccessCookie(String accessToken, boolean persistent) {
    return buildCookieWith(
        COOKIE_NAME, accessToken, Duration.ofMillis(tokenExpiresIn), persistent);
  }

  private ResponseCookie buildRefreshCookie(String refreshToken, boolean persistent) {
    return buildCookieWith(
        REFRESH_COOKIE_NAME, refreshToken, Duration.ofMillis(refreshTokenExpiresIn), persistent);
  }

  private ResponseCookie buildRememberCookie(boolean remember) {
    return buildCookieWith(
        REMEMBER_COOKIE_NAME,
        String.valueOf(remember),
        Duration.ofMillis(refreshTokenExpiresIn),
        remember);
  }

  /**
   * Creates a customer. With email sending configured the account starts unverified: a code is
   * emailed and the response is 202 with no tokens until {@link #verifyEmail} succeeds. Without it,
   * the customer is signed in straight away (201).
   */
  public ResponseEntity<AuthResponse> register(RegisterRequest request) {
    User existing = userRepository.findByEmail(request.getEmail()).orElse(null);
    if (existing != null && (existing.isEmailVerified() || existing.getRole() != UserRole.CUSTOMER)) {
      return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }
    boolean verify = otpService.isAvailable(EmailOtpChannel.CHANNEL);

    // An unverified sign-up is nobody's account yet, so registering again replaces it
    User user = existing != null ? existing : User.builder().build();
    user.setFirstName(request.getFirstName());
    user.setLastName(request.getLastName());
    user.setEmail(request.getEmail());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setActive(true);
    user.setRole(UserRole.CUSTOMER);
    user.setPhone(request.getPhone());
    user.setJoinDate(LocalDate.now());
    user.setEmailVerified(!verify);
    userRepository.save(user);

    if (!verify) {
      return issueTokens(user, true, HttpStatus.CREATED);
    }
    // Throttled: roll back so the replaced details only take effect together with a new code
    if (!otpService.issue(user, OtpPurpose.EMAIL_VERIFICATION, EmailOtpChannel.CHANNEL)) {
      throw new BadRequestException(WAIT_FOR_CODE);
    }
    return ResponseEntity.status(HttpStatus.ACCEPTED).build();
  }

  /** Confirms the emailed code and signs the customer in. */
  public ResponseEntity<AuthResponse> verifyEmail(VerifyEmailRequest request) {
    User user = userRepository.findByEmail(request.getEmail()).orElse(null);
    // One message for unknown, already verified and wrong-code cases, so nothing is revealed
    if (user == null
        || user.isEmailVerified()
        || !otpService.verify(user, OtpPurpose.EMAIL_VERIFICATION, request.getCode())) {
      throw new BadRequestException("The code is invalid or has expired");
    }
    user.setEmailVerified(true);
    return issueTokens(user, true, HttpStatus.OK);
  }

  /** Always 202, whether or not a code was sent, so the endpoint can't be used to probe emails. */
  public ResponseEntity<Void> resendVerification(ResendCodeRequest request) {
    userRepository
        .findByEmail(request.getEmail())
        .filter(this::needsVerification)
        .ifPresent(user -> otpService.issue(user, OtpPurpose.EMAIL_VERIFICATION, EmailOtpChannel.CHANNEL));
    return ResponseEntity.status(HttpStatus.ACCEPTED).build();
  }

  public ResponseEntity<AuthResponse> refresh(String refreshToken, String rememberMe) {
    if (refreshToken == null || refreshToken.isBlank()) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    String username;
    try {
      username = jwtUtil.extractUsername(refreshToken);
    } catch (RuntimeException ex) {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    User user = userRepository.findByEmail(username).orElse(null);
    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

    if (jwtUtil.validateToken(refreshToken, userDetails) && user != null) {
      String newAccessToken = jwtUtil.generateAccessToken(userDetails, user);
      String newRefreshToken = jwtUtil.generateRefreshToken(userDetails);
      // Keep the lifetime the user chose at login
      boolean remember = !"false".equals(rememberMe);
      return ResponseEntity.status(HttpStatus.OK)
          .header(HttpHeaders.SET_COOKIE, buildAccessCookie(newAccessToken, remember).toString())
          .header(HttpHeaders.SET_COOKIE, buildRefreshCookie(newRefreshToken, remember).toString())
          .body(
              new AuthResponse(
                  newAccessToken, tokenExpiresIn, newRefreshToken, refreshTokenExpiresIn));
    } else {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
  }

  public CustomUserDetails getCurrentUserLogin() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth != null && auth.getPrincipal() instanceof CustomUserDetails userDetails) {
      return userDetails;
    }
    return null;
  }

  public ResponseEntity<UserResponse> getMe() {
    var userDetails = getCurrentUserLogin();
    if (userDetails == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    var user =
        userRepository
            .findWithRolesById(userDetails.getId())
            .orElseThrow(() -> new RuntimeException("User not found"));

    var permissions =
        user.getRoles().stream()
            .flatMap(role -> role.getPermissions().stream())
            .map(com.dyc.backendecommerce.permission.Permission::getName)
            .distinct()
            .toList();

    var imageUrl = user.getAsset() != null ? "/api/asset/image/" + user.getAsset().getUuid() : "";
    UserResponse response =
        UserResponse.builder()
            .id(user.getId())
            .firstName(user.getFirstName())
            .lastName(user.getLastName())
            .email(user.getEmail())
            .role(user.getRole())
            .gender(user.getGender())
            .phone(user.getPhone())
            .joinDate(user.getJoinDate())
            .imageUrl(imageUrl)
            .permissions(permissions)
            .build();

    return ResponseEntity.ok(response);
  }

  public ResponseEntity<UserResponse> updateMe(UpdateProfileRequest request) {
    var userDetails = getCurrentUserLogin();
    if (userDetails == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    User user =
        userRepository
            .findById(userDetails.getId())
            .orElseThrow(() -> new RuntimeException("User not found"));

    user.setFirstName(request.getFirstName());
    user.setLastName(request.getLastName());
    user.setPhone(request.getPhone());
    userRepository.save(user);

    return getMe();
  }

  public ResponseEntity<String> changePassword(ChangePasswordRequest request) {
    var userDetails = getCurrentUserLogin();
    if (userDetails == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    User user =
        userRepository
            .findById(userDetails.getId())
            .orElseThrow(() -> new RuntimeException("User not found"));

    if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Current password is incorrect");
    }

    user.setPassword(passwordEncoder.encode(request.getNewPassword()));
    userRepository.save(user);

    return ResponseEntity.ok("Password changed successfully");
  }
}
