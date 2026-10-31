package com.dyc.backendecommerce.role;

import java.util.UUID;

import jakarta.validation.constraints.Size;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleRequest {
  private String name;
  @Size(max = 255, message = "Description must be at most 255 characters")
  private String description;
  private List<UUID> permissionIds;
}
