package com.dyc.backendecommerce.category;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
//    @EntityGraph(attributePaths = {"createdBy", "updatedBy"})
//    Page<Category> findAll(Pageable pageable);
}
