package com.dyc.backendecommerce.order;

import java.util.UUID;

import java.math.BigDecimal;
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
public class OrderItemResponse {
  private UUID id;
  private UUID productId;
  private String productName;
  private String variantName;
  private UUID imageUuid;
  private BigDecimal unitPrice;
  private int quantity;
  private BigDecimal lineTotal;
}
