package com.dyc.backendecommerce.auth;

import com.dyc.backendecommerce.shared.validation.ValidPhone;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UpdateProfileRequest {
  @NotBlank
  @Size(max = 100, message = "First name is too long")
  private String firstName;

  @NotBlank
  @Size(max = 100, message = "Last name is too long")
  private String lastName;

  @ValidPhone private String phone;
}
