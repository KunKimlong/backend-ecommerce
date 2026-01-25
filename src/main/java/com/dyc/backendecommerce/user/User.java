package com.dyc.backendecommerce.user;

import com.dyc.backendecommerce.shared.entity.Auditable;
import com.dyc.backendecommerce.shared.enums.UserRole;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String firstName;
  private String lastName;
  private String email;
  private String password;
  private boolean isActive;
  @Enumerated(EnumType.STRING)
  private UserRole role;

//  private String gender;

}
