package com.dyc.backendecommerce.auth.social;

import com.dyc.backendecommerce.auth.AuthResponse;
import com.dyc.backendecommerce.auth.AuthService;
import com.dyc.backendecommerce.shared.enums.UserRole;
import com.dyc.backendecommerce.shared.exception.NotFoundException;
import com.dyc.backendecommerce.shared.exception.UnAuthorizeException;
import com.dyc.backendecommerce.user.User;
import com.dyc.backendecommerce.user.UserRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Storefront sign-in through an external provider. Provider-specific work (checking the token) lives
 * in a {@link SocialTokenVerifier}; everything after that is the same for every provider.
 */
@Service
@Transactional
public class SocialAuthService {
  // Same message for every refusal, so the endpoint doesn't reveal which accounts exist
  private static final String REFUSED = "This account can't sign in here";

  private final Map<String, SocialTokenVerifier> verifiers;
  private final UserIdentityRepository identityRepository;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final AuthService authService;

  public SocialAuthService(
      List<SocialTokenVerifier> verifiers,
      UserIdentityRepository identityRepository,
      UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      AuthService authService) {
    this.verifiers =
        verifiers.stream()
            .filter(SocialTokenVerifier::isEnabled)
            .collect(Collectors.toUnmodifiableMap(SocialTokenVerifier::provider, Function.identity()));
    this.identityRepository = identityRepository;
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.authService = authService;
  }

  public ResponseEntity<AuthResponse> signIn(String provider, String idToken) {
    SocialTokenVerifier verifier = verifiers.get(provider);
    if (verifier == null) {
      throw new NotFoundException("Sign-in provider not available");
    }
    SocialIdentity identity = verifier.verify(idToken);

    User user =
        identityRepository
            .findByProviderAndSubject(identity.provider(), identity.subject())
            .map(UserIdentity::getUser)
            .orElseGet(() -> linkOrCreate(identity));

    // Staff accounts sign in to the admin app with their password only, never through the storefront
    if (!user.isActive() || user.getRole() != UserRole.CUSTOMER) {
      throw new UnAuthorizeException(REFUSED);
    }
    return authService.issueTokens(user, true, HttpStatus.OK);
  }

  // First sign-in with this provider account: attach it to the customer with the same email, or
  // create that customer. Only a provider-verified email may claim an existing account; otherwise
  // anyone could add an unverified address to their provider account and take over a customer.
  private User linkOrCreate(SocialIdentity identity) {
    if (!identity.emailVerified() || identity.email() == null || identity.email().isBlank()) {
      throw new UnAuthorizeException(REFUSED);
    }
    User user =
        userRepository
            .findByEmailIgnoreCase(identity.email())
            .orElseGet(() -> userRepository.save(newCustomer(identity)));
    if (user.getRole() != UserRole.CUSTOMER) {
      throw new UnAuthorizeException(REFUSED);
    }
    if (!user.isEmailVerified()) {
      // The provider just proved who owns this email. The pending sign-up's password was chosen by
      // whoever registered it, maybe not this person, so it must not keep working.
      user.setEmailVerified(true);
      user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
    }
    identityRepository.save(
        UserIdentity.builder()
            .user(user)
            .provider(identity.provider())
            .subject(identity.subject())
            .build());
    return user;
  }

  private User newCustomer(SocialIdentity identity) {
    String email = identity.email().toLowerCase();
    return User.builder()
        .firstName(
            identity.firstName() != null && !identity.firstName().isBlank()
                ? identity.firstName()
                : email.substring(0, email.indexOf('@')))
        .lastName(identity.lastName())
        .email(email)
        // Spring Security needs a password hash; a random one nobody knows keeps password login closed
        .password(passwordEncoder.encode(UUID.randomUUID().toString()))
        .isActive(true)
        .role(UserRole.CUSTOMER)
        .joinDate(LocalDate.now())
        .build();
  }
}
