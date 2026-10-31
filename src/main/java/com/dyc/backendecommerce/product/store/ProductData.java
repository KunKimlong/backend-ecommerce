package com.dyc.backendecommerce.product.store;

import java.util.UUID;

import com.dyc.backendecommerce.asset.AssetData;
import com.dyc.backendecommerce.category.CategoryData;
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
  private UUID id;
  private String name;
  private String description;
  private BigDecimal salePrice;
  private CategoryData category;
  private Set<ProductVariantData> variants;
  private Set<AssetData> assets;
}
