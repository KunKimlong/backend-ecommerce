package com.dyc.backendecommerce.product.admin;

import com.dyc.backendecommerce.asset.AssetData;
import com.dyc.backendecommerce.category.CategoryData;
import com.dyc.backendecommerce.shared.entity.AuditableResult;
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
public class ProductResponse {
  private Long id;
  private String name;
  private String description;
  private BigDecimal salePrice;
  private CategoryData category;
  private Set<ProductVariantResponse> variants;
  private Set<AssetData> assets;
  private AuditableResult createdBy;
  private AuditableResult updatedBy;
}
