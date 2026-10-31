package com.dyc.backendecommerce.auth;

import com.dyc.backendecommerce.shared.validation.ValidPhone;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
  @NotBlank private String firstName;

  private String lastName;

  @NotBlank
  @Email
  private String email;

  @NotBlank
  @Size(min = 8, message = "Password must be at least 8 characters")
  private String password;

  @NotBlank(message = "Phone number is required")
  @ValidPhone
  private String phone;
}
