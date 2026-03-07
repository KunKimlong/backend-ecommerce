package com.dyc.backendecommerce.product;

import com.dyc.backendecommerce.asset.Asset;
import com.dyc.backendecommerce.category.Category;
import com.dyc.backendecommerce.color.Color;
import com.dyc.backendecommerce.shared.entity.Auditable;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;

@Entity
@Table(name = "products")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Product extends Auditable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String name;
  private String description;
  private BigDecimal importPrice;
  private BigDecimal salePrice;
  private int stockQty;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id", nullable = false)
  private Category category;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "product_colors",
      joinColumns = @JoinColumn(name = "product_id"),
      inverseJoinColumns = @JoinColumn(name = "color_id"))
  private Set<Color> colors;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "product_assets",
      joinColumns = @JoinColumn(name = "product_id"),
      inverseJoinColumns = @JoinColumn(name = "asset_id"))
  private Set<Asset> assets;
}
