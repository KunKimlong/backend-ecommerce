package com.dyc.backendecommerce.user;

import java.util.UUID;

import com.dyc.backendecommerce.role.Role;
import com.dyc.backendecommerce.role.UserRoleData;
import com.dyc.backendecommerce.shared.entity.AuditableResult;
import com.dyc.backendecommerce.shared.enums.Gender;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class UserResponse {
  private UUID id;
  private String firstName;
  private String lastName;
  private String email;
  private Gender gender;
  private List<UserRoleData> roles;
  private boolean active;
  private String phone;
  private LocalDate joinDate;
  private String imageUrl;
  private AuditableResult createdBy;
  private AuditableResult updatedBy;
}
