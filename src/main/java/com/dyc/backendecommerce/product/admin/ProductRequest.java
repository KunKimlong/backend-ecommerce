package com.dyc.backendecommerce.product.admin;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

  @Size(max = 2000, message = "Description must be at most 2000 characters")
  private String description;

  @NotNull private BigDecimal salePrice;

  @NotNull private UUID categoryId;

  private Set<UUID> assetIds;

  @Valid private List<ProductVariantRequest> variants;
}
