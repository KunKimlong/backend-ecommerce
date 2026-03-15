package com.dyc.backendecommerce.asset;

import com.dyc.backendecommerce.shared.entity.AuditableResult;
import com.dyc.backendecommerce.shared.enums.AssetType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class AssetData {
  private Long id;
  private String name;
  private String path;
  private String type;
  private Long size;
  private UUID uuid;
  private AssetType assetType;
  private AuditableResult createdBy;
  private AuditableResult updatedBy;
}
