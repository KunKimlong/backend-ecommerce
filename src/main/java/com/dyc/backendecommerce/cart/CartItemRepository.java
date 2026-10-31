package com.dyc.backendecommerce.cart;

import java.util.UUID;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, UUID> {
  @EntityGraph(
      attributePaths = {
        "productVariant",
        "productVariant.product",
        "productVariant.product.assets",
        "productVariant.optionValues",
        "productVariant.optionValues.option",
        "productVariant.assets"
      })
  List<CartItem> findByUserIdOrderByCreatedAtAsc(UUID userId);

  @EntityGraph(attributePaths = {"productVariant"})
  Optional<CartItem> findByIdAndUserId(UUID id, UUID userId);

  Optional<CartItem> findByUserIdAndProductVariantId(UUID userId, UUID productVariantId);

  void deleteByUserId(UUID userId);

  @Query("select c.productVariant.id from CartItem c where c.user.id = :userId")
  List<UUID> findVariantIdsByUserId(@Param("userId") UUID userId);
}
