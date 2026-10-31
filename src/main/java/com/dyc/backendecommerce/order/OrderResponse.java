package com.dyc.backendecommerce.order;

import java.util.UUID;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
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
public class OrderResponse {
  private UUID id;
  private String orderNumber;
  private OrderStatus status;
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
  private LocalDateTime createdAt;
  private List<OrderItemResponse> items;
}
