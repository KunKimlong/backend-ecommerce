package com.dyc.backendecommerce.product;

import java.util.UUID;

import com.dyc.backendecommerce.asset.Asset;
import java.util.Comparator;
import java.util.Set;

public final class ProductImages {
  private ProductImages() {}

  // Variant photo first, falling back to the product photo; lowest id keeps the pick stable
  public static UUID firstImage(ProductVariant variant) {
    Set<Asset> assets =
        variant.getAssets().isEmpty() ? variant.getProduct().getAssets() : variant.getAssets();
    return assets.stream().min(Comparator.comparing(Asset::getId)).map(Asset::getUuid).orElse(null);
  }
}
