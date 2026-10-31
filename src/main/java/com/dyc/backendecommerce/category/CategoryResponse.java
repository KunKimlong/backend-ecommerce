package com.dyc.backendecommerce.category;

import java.util.UUID;

import com.dyc.backendecommerce.asset.AssetData;
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
public class CategoryResponse {
  private UUID id;
  private String name;
  private AssetData asset;
  private AuditableResult createdBy;
  private AuditableResult updatedBy;
}
