package com.dyc.backendecommerce.product;

import com.dyc.backendecommerce.asset.Asset;
import com.dyc.backendecommerce.category.Category;
import com.dyc.backendecommerce.color.Color;
import java.math.BigDecimal;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
@Data
public class ProductData {
  private Long id;
  private String name;
  private String description;
  private BigDecimal importPrice;
  private BigDecimal salePrice;
  private int stockQty;
  private Category category;
  private Set<Color> colors;
  private Set<Asset> assets;
}
