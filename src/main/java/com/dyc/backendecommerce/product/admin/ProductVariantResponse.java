package com.dyc.backendecommerce.product.admin;

import com.dyc.backendecommerce.asset.AssetData;
import com.dyc.backendecommerce.option.OptionValueData;
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
public class ProductVariantResponse {
  private Long id;
  private String name;
  private BigDecimal price;
  private BigDecimal salePrice;
  private int stockQty;
  private Set<OptionValueData> optionValues;
  private Set<AssetData> assets;
}
