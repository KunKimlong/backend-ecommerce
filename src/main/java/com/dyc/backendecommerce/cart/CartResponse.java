package com.dyc.backendecommerce.cart;

import java.math.BigDecimal;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class CartResponse {
  private List<CartItemResponse> items;
  private int totalQuantity;
  private BigDecimal subtotal;
  private BigDecimal shippingFee;
  private BigDecimal total;
}
