package com.dyc.backendecommerce.product;

import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
  @NonNull
  @EntityGraph(attributePaths = {"category", "variants", "variants.optionValues", "variants.optionValues.option", "variants.assets", "assets"})
  Page<Product> findAll(@NonNull Pageable pageable);

  @NonNull
  @EntityGraph(attributePaths = {"category", "variants", "variants.optionValues", "variants.optionValues.option", "variants.assets", "assets"})
  Optional<Product> findById(@NonNull Long id);
  @NonNull
  @EntityGraph(attributePaths = {"category", "variants", "variants.optionValues", "variants.optionValues.option", "variants.assets", "assets"})
  Page<Product> findByCreatedAtAfterOrderByCreatedAtDesc(
          LocalDateTime date,
          Pageable pageable
  );
}
