package com.dyc.backendecommerce.cart;

import java.util.UUID;

import com.dyc.backendecommerce.optionvalue.OptionValueData;
import java.math.BigDecimal;
import java.util.Set;
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
public class CartItemResponse {
  private UUID id;
  private UUID productId;
  private String productName;
  private UUID variantId;
  private String variantName;
  private Set<OptionValueData> optionValues;
  private UUID imageUuid;
  private BigDecimal price;
  private BigDecimal unitPrice;
  private int quantity;
  private int stockQty;
  private BigDecimal lineTotal;
}
