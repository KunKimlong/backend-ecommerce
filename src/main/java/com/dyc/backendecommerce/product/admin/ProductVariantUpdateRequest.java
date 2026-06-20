package com.dyc.backendecommerce.product.admin;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductVariantUpdateRequest {
  @NotBlank private String name;

  @NotNull private BigDecimal price;

  @NotNull private BigDecimal salePrice;

  @Min(0)
  private int stockQty;

  private Set<Long> optionValueIds;

  private Set<Long> assetIds;
}
