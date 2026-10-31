package com.dyc.backendecommerce.order;

import com.dyc.backendecommerce.shared.validation.ValidPhone;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class PlaceOrderRequest {
  @NotBlank(message = "Full name is required")
  @Size(max = 100, message = "Full name is too long")
  private String fullName;

  @NotBlank(message = "Phone number is required")
  @ValidPhone
  private String phone;

  @NotBlank(message = "Email is required")
  @Email(message = "Email is not valid")
  @Size(max = 255, message = "Email is too long")
  private String email;

  @NotBlank(message = "Address is required")
  @Size(max = 255, message = "Address is too long")
  private String address;

  @NotBlank(message = "Town / City is required")
  @Size(max = 100, message = "Town / City is too long")
  private String city;

  @NotBlank(message = "Country is required")
  @Size(max = 100, message = "Country is too long")
  private String country;

  @Size(max = 500, message = "Notes must be at most 500 characters")
  private String note;

  @NotNull(message = "Payment method is required")
  private PaymentMethod paymentMethod;
}
