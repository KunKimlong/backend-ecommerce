package com.dyc.backendecommerce.user;

import com.dyc.backendecommerce.shared.entity.AuditableResult;
import com.dyc.backendecommerce.shared.enums.Gender;
import com.dyc.backendecommerce.shared.enums.UserRole;
import java.time.LocalDate;
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
  private Long id;
  private String firstName;
  private String lastName;
  private String email;
  private Gender gender;
  private UserRole role;
  private String phone;
  private LocalDate joinDate;
  private String imageUrl;
  private AuditableResult createdBy;
  private AuditableResult updatedBy;
}
