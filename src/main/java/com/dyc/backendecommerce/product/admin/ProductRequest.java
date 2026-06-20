package com.dyc.backendecommerce.product.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequest {
  @NotBlank private String name;

  private String description;

  @NotNull private BigDecimal salePrice;

  @NotNull private Long categoryId;

  private Set<Long> assetIds;

  @Valid private List<ProductVariantRequest> variants;
}
