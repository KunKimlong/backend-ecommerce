package com.dyc.backendecommerce.product;

import com.dyc.backendecommerce.asset.Asset;
import com.dyc.backendecommerce.option.OptionValue;
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
import java.math.BigDecimal;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "product_variants")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductVariant extends Auditable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_id", nullable = false)
  private Product product;

  private String name;
  private BigDecimal price;
  private BigDecimal salePrice;
  private int stockQty;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "product_variant_option_values",
      joinColumns = @JoinColumn(name = "product_variant_id"),
      inverseJoinColumns = @JoinColumn(name = "option_value_id"))
  private Set<OptionValue> optionValues;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
      name = "product_variant_assets",
      joinColumns = @JoinColumn(name = "product_variant_id"),
      inverseJoinColumns = @JoinColumn(name = "asset_id"))
  private Set<Asset> assets;
}
