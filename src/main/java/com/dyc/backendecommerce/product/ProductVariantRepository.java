package com.dyc.backendecommerce.product;

import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {
  @EntityGraph(attributePaths = {"optionValues", "optionValues.option", "assets", "product"})
  Optional<ProductVariant> findWithDetailsById(Long id);
}
