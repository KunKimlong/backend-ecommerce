package com.dyc.backendecommerce.auth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {
  private final AuthService authService;

  @Value("${spring.application.cookie-secure}")
  private boolean cookieSecure;

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
    return authService.login(request);
  }

  @PostMapping("/register")
  public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
    return authService.register(request);
  }

  @PostMapping("/verify-email")
  public ResponseEntity<AuthResponse> verifyEmail(@Valid @RequestBody VerifyEmailRequest request) {
    return authService.verifyEmail(request);
  }

  @PostMapping("/verify-email/resend")
  public ResponseEntity<Void> resendVerification(@Valid @RequestBody ResendCodeRequest request) {
    return authService.resendVerification(request);
  }

  @PostMapping("/refresh")
  public ResponseEntity<AuthResponse> refresh(
      @CookieValue(value = AuthService.REFRESH_COOKIE_NAME, required = false) String refreshToken,
      @CookieValue(value = AuthService.REMEMBER_COOKIE_NAME, required = false) String rememberMe) {
    return authService.refresh(refreshToken, rememberMe);
  }

  @GetMapping("/me")
  public ResponseEntity<UserResponse> getMe() {
    return authService.getMe();
  }

  @PutMapping("/me")
  public ResponseEntity<UserResponse> updateMe(@Valid @RequestBody UpdateProfileRequest request) {
    return authService.updateMe(request);
  }

  @PostMapping("/change-password")
  public ResponseEntity<String> changePassword(
      @Valid @RequestBody ChangePasswordRequest request) {
    return authService.changePassword(request);
  }

  @PostMapping("/logout")
  public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {

    SecurityContextHolder.clearContext();

    HttpSession session = request.getSession(false);
    if (session != null) {
      session.invalidate();
    }

    clearCookie(response, AuthService.COOKIE_NAME);
    clearCookie(response, AuthService.REFRESH_COOKIE_NAME);
    clearCookie(response, AuthService.REMEMBER_COOKIE_NAME);

    return ResponseEntity.ok("Logged out successfully");
  }

  private void clearCookie(HttpServletResponse response, String name) {
    var cookie = new Cookie(name, null);
    cookie.setMaxAge(0);
    cookie.setHttpOnly(true);
    cookie.setSecure(cookieSecure);
    cookie.setPath("/");
    response.addCookie(cookie);
  }
}
