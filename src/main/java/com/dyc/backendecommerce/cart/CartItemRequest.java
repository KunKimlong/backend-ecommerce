package com.dyc.backendecommerce.cart;

import java.util.UUID;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
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
public class CartItemRequest {
  @NotNull(message = "Variant is required")
  private UUID variantId;

  @Min(value = 1, message = "Quantity must be at least 1")
  @Max(value = CartService.MAX_QUANTITY, message = "Quantity must be at most 99")
  private int quantity;
}
