package com.dyc.backendecommerce.wishlist;

import java.util.UUID;

import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WishlistItemRepository extends JpaRepository<WishlistItem, UUID> {
  @EntityGraph(
      attributePaths = {
        "product",
        "product.category",
        "product.assets",
        "product.variants",
        "product.variants.optionValues",
        "product.variants.optionValues.option",
        "product.variants.assets"
      })
  List<WishlistItem> findByUserIdOrderByCreatedAtDesc(UUID userId);

  boolean existsByUserIdAndProductId(UUID userId, UUID productId);

  void deleteByUserIdAndProductId(UUID userId, UUID productId);
}
