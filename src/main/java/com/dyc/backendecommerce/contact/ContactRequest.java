package com.dyc.backendecommerce.contact;

import com.dyc.backendecommerce.shared.validation.ValidPhone;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContactRequest {
  @NotBlank(message = "Name is required")
  @Size(max = 100, message = "Name is too long")
  private String name;

  @NotBlank(message = "Email is required")
  @Email(message = "Email is not valid")
  @Size(max = 255, message = "Email is too long")
  private String email;

  @ValidPhone
  private String phone;

  @NotBlank(message = "Subject is required")
  @Size(max = 150, message = "Subject is too long")
  private String subject;

  @NotBlank(message = "Message is required")
  @Size(max = 2000, message = "Message must be at most 2000 characters")
  private String message;
}
