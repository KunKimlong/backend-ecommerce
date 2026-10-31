package com.dyc.backendecommerce.role;

import java.util.UUID;

import com.dyc.backendecommerce.permission.PermissionData;
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
  private UUID id;
  private String name;
  private String description;
  private List<PermissionData> permissions;
}
