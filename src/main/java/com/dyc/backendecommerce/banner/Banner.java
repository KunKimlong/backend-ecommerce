package com.dyc.backendecommerce.banner;

import com.dyc.backendecommerce.asset.Asset;
import com.dyc.backendecommerce.banner_type.BannerType;
import com.dyc.backendecommerce.product.Product;
import com.dyc.backendecommerce.shared.entity.Auditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "banners")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Banner extends Auditable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "label", length = 100)
  private String label;

  @Column(name = "header_label", length = 50)
  private String headerLabel;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "banner_type_id")
  private BannerType bannerType;

  @Column(name = "description", columnDefinition = "text")
  private String description;

  @Column(name = "button_name", length = 20)
  private String buttonName;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id")
  private Product product;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "asset_id")
  private Asset asset;

  @Column(name = "start_at")
  private LocalDate startAt;

  @Column(name = "end_at")
  private LocalDate endAt;

  @Column(name = "banner_order")
  private Integer order;
}
