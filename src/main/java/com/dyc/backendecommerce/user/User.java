package com.dyc.backendecommerce.user;

import java.util.UUID;
import com.dyc.backendecommerce.shared.entity.UuidV7;

import com.dyc.backendecommerce.asset.Asset;
import com.dyc.backendecommerce.role.Role;
import com.dyc.backendecommerce.shared.entity.Auditable;
import com.dyc.backendecommerce.shared.enums.Gender;
import com.dyc.backendecommerce.shared.enums.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Entity
@Setter
@Getter
@Builder
@Table(name = "users")
public class User extends Auditable {
  @Id
  @UuidV7
  private UUID id;
  private String firstName;
  private String lastName;
  private String email;
  private String password;
  private boolean isActive;

  // False only for self-registered customers who haven't entered their emailed code yet
  @Builder.Default
  private boolean emailVerified = true;

  // Tokens issued before this are no longer accepted (set by a password reset)
  private Instant credentialsChangedAt;
  @Enumerated(EnumType.STRING)
  private UserRole role;
  @Enumerated(EnumType.STRING)
  private Gender gender;

  @Column(length = 20)
  private String phone;

  private LocalDate joinDate;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "asset_id")
  private Asset asset;

  @ManyToMany
  @JoinTable(
      name = "user_roles",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "role_id"))
  @Builder.Default
  private Set<Role> roles = new HashSet<>();
}
