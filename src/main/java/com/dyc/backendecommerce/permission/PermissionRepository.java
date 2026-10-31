package com.dyc.backendecommerce.permission;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, UUID> {

  List<Permission> findByIdIn(List<UUID> ids);

  Page<Permission> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
