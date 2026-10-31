package com.dyc.backendecommerce.order;

import java.util.UUID;
import com.dyc.backendecommerce.shared.entity.UuidV7;

import com.dyc.backendecommerce.product.ProductVariant;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** A purchased line; names and prices are copied so the order stays readable if the product changes. */
@Entity
@Table(name = "order_items")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderItem {
  @Id
  @UuidV7
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id", nullable = false)
  private Order order;

  // Null once the variant is deleted; the snapshot columns below still describe the line
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "product_variant_id")
  private ProductVariant productVariant;

  private UUID productId;
  private String productName;
  private String variantName;
  private UUID imageUuid;
  private BigDecimal unitPrice;
  private int quantity;
  private BigDecimal lineTotal;
}
