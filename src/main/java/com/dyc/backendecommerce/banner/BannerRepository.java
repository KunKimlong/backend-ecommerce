package com.dyc.backendecommerce.banner;

import java.util.UUID;

import java.time.LocalDate;
import lombok.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BannerRepository extends JpaRepository<Banner, UUID> {
  @NonNull
  @EntityGraph(attributePaths = {"product"})
  Page<Banner> findAll(@NonNull Pageable pageable);

  @NonNull
  @EntityGraph(attributePaths = {"product"})
  Page<Banner> findByLabelContainingIgnoreCase(@NonNull String label, @NonNull Pageable pageable);

  @NonNull
  @EntityGraph(attributePaths = {"product"})
  Page<Banner> findByPlacementAndStartAtLessThanEqualAndEndAtGreaterThanEqual(
      @NonNull BannerPlacement placement,
      @NonNull LocalDate startAt,
      @NonNull LocalDate endAt,
      @NonNull Pageable pageable);
}
