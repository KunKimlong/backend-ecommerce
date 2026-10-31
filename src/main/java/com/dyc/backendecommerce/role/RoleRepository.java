package com.dyc.backendecommerce.role;

import java.util.UUID;

import lombok.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {

  @EntityGraph(attributePaths = {"permissions"})
  @NonNull
  List<Role> findAll();

  @EntityGraph(attributePaths = {"permissions"})
  @NonNull
  Optional<Role> findById(@NonNull UUID id);

  Optional<Role> findByName(String name);

  Page<Role> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
