package com.dyc.backendecommerce.employee;

import java.time.LocalDate;

import com.dyc.backendecommerce.shared.entity.AuditableResult;
import com.dyc.backendecommerce.shared.enums.Gender;
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
public class EmployeeData {
  private Long id;
  private String firstName;
  private String lastName;
  private String email;
  private Gender gender;
  private String phone;
  private LocalDate joinDate;
  private String imageUrl;
  private AuditableResult createdBy;
  private AuditableResult updatedBy;
}
