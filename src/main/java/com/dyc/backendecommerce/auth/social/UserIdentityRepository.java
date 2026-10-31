package com.dyc.backendecommerce.auth.social;

import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserIdentityRepository extends JpaRepository<UserIdentity, UUID> {

  @EntityGraph(attributePaths = {"user"})
  Optional<UserIdentity> findByProviderAndSubject(String provider, String subject);
}
