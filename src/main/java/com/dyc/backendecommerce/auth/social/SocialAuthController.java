package com.dyc.backendecommerce.auth.social;

import com.dyc.backendecommerce.auth.AuthResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Public (under /api/auth/**): the caller proves who they are with the provider's signed token
@RestController
@RequestMapping("api/auth/social")
@RequiredArgsConstructor
public class SocialAuthController {
  private final SocialAuthService socialAuthService;

  @PostMapping("/{provider}")
  public ResponseEntity<AuthResponse> signIn(
      @PathVariable String provider, @Valid @RequestBody SocialSignInRequest request) {
    return socialAuthService.signIn(provider, request.getIdToken());
  }
}
