package com.dyc.backendecommerce.auth.social;

/**
 * Who a provider says signed in, taken from a token whose signature, issuer, audience and expiry
 * have already been checked. {@code subject} is the provider's permanent id for the account.
 */
public record SocialIdentity(
    String provider,
    String subject,
    String email,
    boolean emailVerified,
    String firstName,
    String lastName) {}
