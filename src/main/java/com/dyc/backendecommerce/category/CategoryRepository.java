package com.dyc.backendecommerce.category;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    Page<Category> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @Query("select c.id from Category c where lower(c.name) like :pattern escape '\\'")
    List<UUID> findIdsByLowerNameLike(@Param("pattern") String pattern);
}
