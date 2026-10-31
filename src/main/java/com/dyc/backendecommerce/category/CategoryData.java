package com.dyc.backendecommerce.category;

import java.util.UUID;

import com.dyc.backendecommerce.asset.AssetData;
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
public class CategoryData {
  private UUID id;
  private String name;
  private AssetData asset;
}
