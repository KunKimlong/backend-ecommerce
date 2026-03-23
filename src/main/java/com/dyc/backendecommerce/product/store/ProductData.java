package com.dyc.backendecommerce.product.store;

import com.dyc.backendecommerce.asset.AssetData;
import com.dyc.backendecommerce.category.CategoryData;
import com.dyc.backendecommerce.color.ColorData;
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
public class ProductData {
  private Long id;
  private String name;
  private String description;
  private BigDecimal importPrice;
  private BigDecimal salePrice;
  private int stockQty;
  private CategoryData category;
  private Set<ColorData> colors;
  private Set<AssetData> assets;
}
