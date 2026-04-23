package com.dyc.backendecommerce.store.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StoreRequest {
  private Long assetId;
  private String name;
  private String description;
  private String location;
  private String supportEmail;
  private String supportPhoneNumber;
  private Boolean isActive;
}
