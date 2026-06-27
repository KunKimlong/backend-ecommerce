package com.dyc.backendecommerce.role;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

  @EntityGraph(attributePaths = {"permissions"})
  List<Role> findAll();

  @EntityGraph(attributePaths = {"permissions"})
  Optional<Role> findById(Long id);

  Optional<Role> findByName(String name);

  Page<Role> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
