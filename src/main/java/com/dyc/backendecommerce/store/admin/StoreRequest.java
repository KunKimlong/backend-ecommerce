package com.dyc.backendecommerce.store.admin;

import java.util.UUID;

import com.dyc.backendecommerce.shared.validation.ValidPhone;
import jakarta.validation.constraints.Size;
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
  private UUID assetId;
  private String name;
  @Size(max = 500, message = "Description must be at most 500 characters")
  private String description;
  private String location;
  private String supportEmail;
  @ValidPhone private String supportPhoneNumber;
  private Boolean isActive;
  // Clear the dark logo / favicon (a newly uploaded file takes precedence)
  private boolean removeDarkLogo;
  private boolean removeFavicon;
}
