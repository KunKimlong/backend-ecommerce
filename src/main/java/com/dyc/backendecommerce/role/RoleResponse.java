package com.dyc.backendecommerce.role;

import com.dyc.backendecommerce.permission.PermissionData;
import com.dyc.backendecommerce.shared.entity.AuditableResult;
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
public class RoleResponse {
  private Long id;
  private String name;
  private String description;
  private List<PermissionData> permissions;
  private AuditableResult createdBy;
  private AuditableResult updatedBy;
}
