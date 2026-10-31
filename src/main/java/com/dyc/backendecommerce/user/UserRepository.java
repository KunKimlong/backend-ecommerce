package com.dyc.backendecommerce.user;

import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {

    @EntityGraph(attributePaths = {"roles.permissions"})
    Optional<User> findByEmail(String email);

    Optional<User> findByEmailIgnoreCase(String email);

    @EntityGraph(attributePaths = {"roles.permissions"})
    Optional<User> findWithRolesById(UUID id);
}
