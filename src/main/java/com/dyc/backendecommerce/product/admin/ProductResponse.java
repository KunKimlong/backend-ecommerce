package com.dyc.backendecommerce.product.admin;

import com.dyc.backendecommerce.asset.Asset;
import com.dyc.backendecommerce.category.Category;
import com.dyc.backendecommerce.color.Color;
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
  private BigDecimal importPrice;
  private BigDecimal salePrice;
  private int stockQty;
  private Category category;
  private Set<Color> colors;
  private Set<Asset> assets;
  private AuditableResult createdBy;
  private AuditableResult updatedBy;
}
