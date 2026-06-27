package com.dyc.backendecommerce.banner.admin;

import com.dyc.backendecommerce.asset.AssetData;
import com.dyc.backendecommerce.product.admin.ProductResponse;
import java.time.LocalDateTime;
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
public class BannerData {
  private Long id;
  private String label;
  private String headerLabel;
    private Long bannerTypeId;
    private String type;
    private String description;
  private String buttonName;
  private ProductResponse product;
  private AssetData asset;
    private Integer order;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
}
