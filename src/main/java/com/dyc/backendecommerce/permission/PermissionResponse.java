package com.dyc.backendecommerce.permission;

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
public class PermissionResponse {
  private Long id;
  private String name;
  private String description;
  private String module;
  private AuditableResult createdBy;
  private AuditableResult updatedBy;
}
