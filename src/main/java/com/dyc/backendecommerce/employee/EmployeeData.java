package com.dyc.backendecommerce.employee;

import java.time.LocalDate;

import com.dyc.backendecommerce.shared.entity.AuditableResult;
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
  private Long gender;
  private String phone;
  private LocalDate joinDate;
  private AuditableResult createdBy;
  private AuditableResult updatedBy;
}
