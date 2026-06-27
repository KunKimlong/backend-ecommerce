package com.dyc.backendecommerce.banner.admin;

import com.dyc.backendecommerce.asset.AssetData;
import com.dyc.backendecommerce.product.store.ProductData;
import com.dyc.backendecommerce.shared.entity.AuditableResult;
import java.time.LocalDate;
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
public class BannerResponse {
  private Long id;
  private String label;
  private String headerLabel;
  private Long bannerTypeId;
  private String type;
  private String description;
  private String buttonName;
  private ProductData product;
  private AssetData asset;
  private Integer order;
  private LocalDate startAt;
  private LocalDate endAt;
  private AuditableResult createdBy;
  private AuditableResult updatedBy;
}
