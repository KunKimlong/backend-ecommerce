package com.dyc.backendecommerce.shared.config;

import com.dyc.backendecommerce.shared.entity.CustomUserDetails;
import com.dyc.backendecommerce.shared.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

  private final JwtUtil jwtUtil;
  private final UserDetailsService userDetailsService;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain chain)
      throws ServletException, IOException {

    String username = null;
    String jwt = null;

    // ✅ 1. Try to extract JWT from Authorization header
    final String authHeader = request.getHeader("Authorization");
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      jwt = authHeader.substring(7);
      log.debug("JWT extracted from Authorization header");
    }

    // ✅ 2. If not in header, try to extract from cookies
    if (jwt == null && request.getCookies() != null) {
      for (Cookie cookie : request.getCookies()) {
        // Check for common cookie names
        if ("authToken".equals(cookie.getName())) {
          jwt = cookie.getValue();
          break;
        }
      }
    }

    // ✅ 3. Extract username from JWT if found
    if (jwt != null) {
      try {
        username = jwtUtil.extractUsername(jwt);
      } catch (Exception e) {
      }
    }

    // ✅ 4. Authenticate if username is valid and no authentication exists
    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
      try {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        if (jwtUtil.validateToken(jwt, userDetails)) {
          Long userId = jwtUtil.extractUserId(jwt);
          CustomUserDetails customUser =
              new CustomUserDetails(
                  userId,
                  userDetails.getUsername(),
                  userDetails.getPassword(),
                  userDetails.getAuthorities());

          UsernamePasswordAuthenticationToken authToken =
              new UsernamePasswordAuthenticationToken(
                  customUser, null, userDetails.getAuthorities());
          authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
          SecurityContextHolder.getContext().setAuthentication(authToken);

          log.debug("User authenticated: {}", username);
        } else {
          log.warn("JWT validation failed for user: {}", username);
        }
      } catch (Exception e) {
        log.error("Authentication error: {}", e.getMessage());
      }
    }

    chain.doFilter(request, response);
  }
}
