package com.dyc.backendecommerce.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest {
  @NotBlank private String name;

  private String description;

  @NotNull private BigDecimal importPrice;

  @NotNull private BigDecimal salePrice;

  @Min(0)
  private int stockQty;

  @NotNull private Long categoryId;

  private Set<Long> colorIds;

  private Set<Long> assetIds;
}
