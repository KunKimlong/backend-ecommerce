package com.dyc.backendecommerce.shared.util;

import java.util.UUID;

import com.dyc.backendecommerce.shared.entity.CustomUserDetails;
import com.dyc.backendecommerce.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
  @Value("${spring.application.jwt.secret-key}")
  private String secretKey;

  @Value("${spring.application.jwt.access-token-exp}")
  private long accessTokenExpired;

  @Value("${spring.application.jwt.refresh-token-exp}")
  private long refreshTokenExpired;

  public String generateAccessToken(UserDetails userDetails, User user) {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    Key key = Keys.hmacShaKeyFor(keyBytes);
    return Jwts.builder()
        .setSubject(userDetails.getUsername())
        .claim("userId", user.getId().toString())
        .claim("role", userDetails.getAuthorities().iterator().next().getAuthority())
        .claim("email", user.getEmail())
        .claim("firstName", user.getFirstName())
        .claim("lastName", user.getLastName())
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + accessTokenExpired))
        .signWith(key, SignatureAlgorithm.HS512)
        .compact();
  }

  public String generateRefreshToken(UserDetails userDetails) {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    Key key = Keys.hmacShaKeyFor(keyBytes);
    return Jwts.builder()
        .setSubject(userDetails.getUsername())
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + refreshTokenExpired))
        .signWith(key, SignatureAlgorithm.HS512)
        .compact();
  }

  public String extractUsername(String token) {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    Key key = Keys.hmacShaKeyFor(keyBytes);
    return Jwts.parserBuilder()
        .setSigningKey(key)
        .build()
        .parseClaimsJws(token)
        .getBody()
        .getSubject();
  }

  public UUID extractUserId(String token) {
    return extractClaim(token, claims -> UUID.fromString(claims.get("userId", String.class)));
  }

  public <T> T extractClaim(String token, Function<Claims, T> resolver) {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    Key key = Keys.hmacShaKeyFor(keyBytes);
    Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
    return resolver.apply(claims);
  }

  public boolean validateToken(String token, UserDetails userDetails) {
    String username = extractUsername(token);
    return username.equals(userDetails.getUsername())
        && !isTokenExpired(token)
        && !issuedBeforeCredentialsChanged(token, userDetails);
  }

  // A password reset ends every session that existed before it. iat has whole-second precision,
  // which is why the reset time is stored truncated to seconds.
  private boolean issuedBeforeCredentialsChanged(String token, UserDetails userDetails) {
    if (!(userDetails instanceof CustomUserDetails details) || details.getCredentialsChangedAt() == null) {
      return false;
    }
    Date issuedAt = extractClaim(token, Claims::getIssuedAt);
    return issuedAt == null || issuedAt.toInstant().isBefore(details.getCredentialsChangedAt());
  }

  private boolean isTokenExpired(String token) {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    Key key = Keys.hmacShaKeyFor(keyBytes);
    Date expiration =
        Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getExpiration();
    return expiration.before(new Date());
  }
}
