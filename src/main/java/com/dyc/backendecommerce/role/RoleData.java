package com.dyc.backendecommerce.role;

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
public class RoleData {
  private Long id;
  private String name;
  private String description;
  private List<Long> permissionIds;
}
