package com.dyc.backendecommerce.cart;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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
public class CartQuantityRequest {
  @Min(value = 1, message = "Quantity must be at least 1")
  @Max(value = CartService.MAX_QUANTITY, message = "Quantity must be at most 99")
  private int quantity;
}
