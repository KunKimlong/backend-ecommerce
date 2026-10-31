package com.dyc.backendecommerce.auth.social;

import com.dyc.backendecommerce.shared.exception.UnAuthorizeException;
import java.util.Set;
import java.util.function.Predicate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtClaimNames;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.stereotype.Component;

/**
 * Checks a Google ID token the way Google documents it: RS256 signature against Google's published
 * keys (fetched and cached by Nimbus), issued by Google, issued to our client id, and not expired.
 * The token reaches us from the storefront's server, but it is verified here regardless of who sent it.
 */
@Component
public class GoogleTokenVerifier implements SocialTokenVerifier {
  private static final String JWK_SET_URI = "https://www.googleapis.com/oauth2/v3/certs";
  // Google uses both spellings of its issuer
  private static final Set<String> ISSUERS = Set.of("https://accounts.google.com", "accounts.google.com");

  private final String clientId;
  private final JwtDecoder decoder;

  public GoogleTokenVerifier(@Value("${spring.application.social.google.client-id:}") String clientId) {
    this.clientId = clientId;
    NimbusJwtDecoder nimbus = NimbusJwtDecoder.withJwkSetUri(JWK_SET_URI).build();
    nimbus.setJwtValidator(
        new DelegatingOAuth2TokenValidator<>(
            new JwtTimestampValidator(),
            claim("issuer", jwt -> ISSUERS.contains(jwt.getClaimAsString(JwtClaimNames.ISS))),
            // Without this, a token Google issued to any other website would be accepted
            claim("audience", jwt -> jwt.getAudience() != null && jwt.getAudience().contains(clientId))));
    this.decoder = nimbus;
  }

  @Override
  public String provider() {
    return "google";
  }

  @Override
  public boolean isEnabled() {
    return !clientId.isBlank();
  }

  @Override
  public SocialIdentity verify(String token) {
    Jwt jwt;
    try {
      jwt = decoder.decode(token);
    } catch (JwtException e) {
      throw new UnAuthorizeException("Google sign-in could not be verified");
    }
    return new SocialIdentity(
        provider(),
        jwt.getSubject(),
        jwt.getClaimAsString("email"),
        Boolean.TRUE.equals(jwt.getClaimAsBoolean("email_verified")),
        jwt.getClaimAsString("given_name"),
        jwt.getClaimAsString("family_name"));
  }

  private static OAuth2TokenValidator<Jwt> claim(String name, Predicate<Jwt> check) {
    OAuth2Error error = new OAuth2Error("invalid_token", "Unexpected " + name, null);
    return jwt -> check.test(jwt) ? OAuth2TokenValidatorResult.success() : OAuth2TokenValidatorResult.failure(error);
  }
}
