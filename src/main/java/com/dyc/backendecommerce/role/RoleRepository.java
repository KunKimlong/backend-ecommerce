package com.dyc.backendecommerce.role;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

  @EntityGraph(attributePaths = {"permissions"})
  List<Role> findAll();

  @EntityGraph(attributePaths = {"permissions"})
  Optional<Role> findById(Long id);

  Optional<Role> findByName(String name);
}
