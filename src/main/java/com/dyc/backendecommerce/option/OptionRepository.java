package com.dyc.backendecommerce.option;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OptionRepository extends JpaRepository<Option, Long> {
    Page<Option> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
