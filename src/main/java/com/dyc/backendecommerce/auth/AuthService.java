package com.dyc.backendecommerce.auth;

import com.dyc.backendecommerce.shared.entity.CustomUserDetails;
import com.dyc.backendecommerce.shared.util.JwtUtil;
import com.dyc.backendecommerce.user.User;
import com.dyc.backendecommerce.user.UserRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@Transactional
@RequiredArgsConstructor
public class AuthService {
  @Value("${spring.application.jwt.access-toke-exp}")
  private int tokenExpiresIn;

  @Value("${spring.application.jwt.refresh-toke-exp}")
  private int refreshTokenExpiresIn;

  private final UserRepository userRepository;
  private final AuthenticationManager authenticationManager;
  private final JwtUtil jwtUtil;
  private final UserDetailsService userDetailsService;

  public ResponseEntity<?> login(AuthRequest request) {
    User user = userRepository.findByEmail(request.getUsername()).orElse(null);
    if (user == null) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

    UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
    String accessToken = jwtUtil.generateAccessToken(userDetails, user);
    String refreshToken = jwtUtil.generateRefreshToken(userDetails);

    return ResponseEntity.ok(
        new AuthResponse(accessToken, tokenExpiresIn, refreshToken, refreshTokenExpiresIn));
  }

  public ResponseEntity<?> refresh(String refreshToken) {
    String username = jwtUtil.extractUsername(refreshToken);
    User user = userRepository.findByEmail(username).orElse(null);
    UserDetails userDetails = userDetailsService.loadUserByUsername(username);

    if (jwtUtil.validateToken(refreshToken, userDetails)) {
      String newAccessToken = jwtUtil.generateAccessToken(userDetails, user);
      return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    } else {
      return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }
  }

  public CustomUserDetails getCurrentUserLogin() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    return (CustomUserDetails) auth.getPrincipal();
  }
}
