package com.dyc.backendecommerce.permission;

import java.util.UUID;

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
public class PermissionData {
  private UUID id;
  private String name;
  private String description;
  private String module;
}
