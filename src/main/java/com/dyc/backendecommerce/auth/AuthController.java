package com.dyc.backendecommerce.auth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {
  private final AuthService authService;

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
    return authService.login(request);
  }

  @PostMapping("/refresh")
  public ResponseEntity<AuthResponse> refresh(@RequestParam String refreshToken) {
    return authService.refresh(refreshToken);
  }

  @GetMapping("/me")
  public ResponseEntity<UserResponse> getMe() {
    return authService.getMe();
  }

  @PostMapping("/logout")
  public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) {

    SecurityContextHolder.clearContext();

    HttpSession session = request.getSession(false);
    if (session != null) {
      session.invalidate();
    }

    var cookie = new Cookie("authToken", null);
    cookie.setMaxAge(0);
    cookie.setHttpOnly(true);
    cookie.setSecure(false);
    cookie.setPath("/");
    response.addCookie(cookie);

    var authRefreshToken = new Cookie("refreshToken", null);
    authRefreshToken.setMaxAge(0);
    authRefreshToken.setHttpOnly(true);
    authRefreshToken.setSecure(false);
    authRefreshToken.setPath("/");
    response.addCookie(authRefreshToken);

    return ResponseEntity.ok("Logged out successfully");
  }
}
