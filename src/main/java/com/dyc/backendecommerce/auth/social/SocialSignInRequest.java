package com.dyc.backendecommerce.auth.social;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SocialSignInRequest {
  // The provider's signed ID token; ID tokens are a few KB at most
  @NotBlank
  @Size(max = 8192)
  private String idToken;
}
