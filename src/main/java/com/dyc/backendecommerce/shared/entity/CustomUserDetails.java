package com.dyc.backendecommerce.shared.entity;

import java.time.Instant;
import java.util.UUID;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class CustomUserDetails extends User {
  private final UUID id;
  // Tokens issued before this instant are rejected; null when the password was never reset
  private final Instant credentialsChangedAt;

  public CustomUserDetails(
      UUID id,
      String username,
      String password,
      Collection<? extends GrantedAuthority> authorities) {
    this(id, username, password, authorities, null);
  }

  public CustomUserDetails(
      UUID id,
      String username,
      String password,
      Collection<? extends GrantedAuthority> authorities,
      Instant credentialsChangedAt) {

    super(username, password, authorities);
    this.id = id;
    this.credentialsChangedAt = credentialsChangedAt;
  }

}
