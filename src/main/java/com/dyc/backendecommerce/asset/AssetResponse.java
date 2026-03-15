package com.dyc.backendecommerce.asset;

import com.dyc.backendecommerce.color.ColorData;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class AssetResponse {
  List<AssetData> assetData;
  private long total;
  private int page;
  private int pageSize;
}
