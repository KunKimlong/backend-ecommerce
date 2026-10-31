package com.dyc.backendecommerce.order;

import java.util.UUID;
import com.dyc.backendecommerce.shared.entity.UuidV7;

import com.dyc.backendecommerce.shared.entity.Auditable;
import com.dyc.backendecommerce.user.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;

@Entity
@Table(name = "orders")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Order extends Auditable {
  @Id
  @UuidV7
  private UUID id;

  @Column(nullable = false, unique = true, length = 20)
  private String orderNumber;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  private OrderStatus status;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 30)
  private PaymentMethod paymentMethod;

  private String fullName;
  private String phone;
  private String email;
  private String address;
  private String city;
  private String country;
  private String note;

  private BigDecimal subtotal;
  private BigDecimal shippingFee;
  private BigDecimal total;

  // Batch-loaded so a page of orders fetches all its items in one extra query
  @OneToMany(mappedBy = "order", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
  @OrderBy("productName")
  @BatchSize(size = 50)
  @Builder.Default
  private List<OrderItem> items = new ArrayList<>();
}
