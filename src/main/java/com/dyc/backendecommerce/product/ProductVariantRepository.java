package com.dyc.backendecommerce.product;

import java.util.UUID;

import jakarta.persistence.LockModeType;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, UUID> {
  @EntityGraph(attributePaths = {"optionValues", "optionValues.option", "assets", "product"})
  Optional<ProductVariant> findWithDetailsById(UUID id);

  // Row locks taken in id order so two concurrent checkouts can't deadlock or oversell
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select v from ProductVariant v where v.id in :ids order by v.id")
  List<ProductVariant> findAllForUpdate(@Param("ids") Collection<UUID> ids);
}
