package com.dyc.backendecommerce.auth.social;

/**
 * Verifies the sign-in token of one external provider. To support another provider, add a Spring
 * bean implementing this; {@link SocialAuthService} picks it up and serves it at
 * {@code POST /api/auth/social/{provider()}} with no other backend change.
 */
public interface SocialTokenVerifier {

  /** Lower-case name used in the URL and stored with linked accounts, e.g. {@code "google"}. */
  String provider();

  /** False when the provider isn't configured; disabled providers are treated as unknown. */
  boolean isEnabled();

  /**
   * Returns the verified identity, or throws {@link
   * com.dyc.backendecommerce.shared.exception.UnAuthorizeException} for a token that is forged,
   * expired, or issued to a different app.
   */
  SocialIdentity verify(String token);
}
