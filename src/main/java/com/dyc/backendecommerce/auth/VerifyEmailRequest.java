package com.dyc.backendecommerce.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class VerifyEmailRequest {
  @NotBlank @Email private String email;

  @NotBlank
  @Pattern(regexp = "\\d{6}", message = "Enter the 6-digit code from the email")
  private String code;
}
