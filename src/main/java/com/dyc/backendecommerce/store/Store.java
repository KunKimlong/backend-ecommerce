package com.dyc.backendecommerce.store;

import java.util.UUID;
import com.dyc.backendecommerce.shared.entity.UuidV7;

import com.dyc.backendecommerce.asset.Asset;
import com.dyc.backendecommerce.shared.entity.Auditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "stores")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Store extends Auditable {
  @Id
  @UuidV7
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "asset_id")
  private Asset asset;

  // Shown on dark backgrounds; null = the regular logo is used there too
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "dark_logo_asset_id")
  private Asset darkLogoAsset;

  // Browser-tab icon for both apps; null = their built-in icon
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "favicon_asset_id")
  private Asset faviconAsset;

  @Column(name = "name", length = 255)
  private String name;

  @Column(name = "description", columnDefinition = "text")
  private String description;

  @Column(name = "location", length = 255)
  private String location;

  @Column(name = "support_email", length = 255)
  private String supportEmail;

  @Column(name = "support_phone_number", length = 255)
  private String supportPhoneNumber;
}
