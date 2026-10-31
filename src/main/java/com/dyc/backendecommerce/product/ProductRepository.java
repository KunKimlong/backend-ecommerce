package com.dyc.backendecommerce.product;

import java.util.UUID;

import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID>, JpaSpecificationExecutor<Product> {
  // Second step of a paged catalog query: load the page's products with everything the card needs
  @EntityGraph(attributePaths = {"category", "variants", "variants.optionValues", "variants.optionValues.option", "variants.assets", "assets"})
  List<Product> findAllByIdIn(Collection<UUID> ids);

  // Units sold per product across orders that weren't cancelled; deleted products drop out
  @Query(
      "select i.productId from OrderItem i"
          + " where i.order.status <> com.dyc.backendecommerce.order.OrderStatus.CANCELLED"
          + " and exists (select p.id from Product p where p.id = i.productId)"
          + " group by i.productId order by sum(i.quantity) desc")
  List<UUID> findBestSellingIds(Pageable pageable);

  @NonNull
  @EntityGraph(attributePaths = {"category", "variants", "variants.optionValues", "variants.optionValues.option", "variants.assets", "assets"})
  Optional<Product> findById(@NonNull UUID id);

  // Paged queries return bare products (first step); details come from findAllByIdIn
  @NonNull
  Page<Product> findByCreatedAtAfterOrderByCreatedAtDesc(
          LocalDateTime date,
          Pageable pageable
  );

  @NonNull
  Page<Product> findByNameContainingIgnoreCase(@NonNull String name, @NonNull Pageable pageable);
}
