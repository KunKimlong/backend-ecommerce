package com.dyc.backendecommerce.store.admin;

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
  private Long id;
  private Long assetId;
  private String logoPath;
  private String name;
  private String description;
  private String location;
  private String supportEmail;
  private String supportPhoneNumber;
  private Boolean isActive;
  private AuditableResult createdBy;
  private AuditableResult updatedBy;
}
