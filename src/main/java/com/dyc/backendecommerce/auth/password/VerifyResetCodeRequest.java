package com.dyc.backendecommerce.auth.password;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class VerifyResetCodeRequest {
  @NotBlank @Email private String email;

  @NotBlank
  @Pattern(regexp = "\\d{6}", message = "Enter the 6-digit code from the email")
  private String code;
}
