package com.dyc.backendecommerce.store.admin;

import java.util.UUID;

import com.dyc.backendecommerce.shared.entity.AuditableResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class StoreResponse {
  private UUID id;
  private UUID assetId;
  private String logoPath;
  private String darkLogoPath;
  private String faviconPath;
  private String name;
  private String description;
  private String location;
  private String supportEmail;
  private String supportPhoneNumber;
  private Boolean isActive;
  private AuditableResult createdBy;
  private AuditableResult updatedBy;
}
