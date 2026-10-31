package com.dyc.backendecommerce.order;

import java.util.UUID;

import com.dyc.backendecommerce.cart.CartItem;
import com.dyc.backendecommerce.cart.CartItemRepository;
import com.dyc.backendecommerce.cart.CartService;
import com.dyc.backendecommerce.product.ProductImages;
import com.dyc.backendecommerce.product.ProductVariant;
import com.dyc.backendecommerce.product.ProductVariantRepository;
import com.dyc.backendecommerce.shared.config.CacheConfig;
import com.dyc.backendecommerce.shared.exception.BadRequestException;
import com.dyc.backendecommerce.shared.exception.NotFoundException;
import com.dyc.backendecommerce.shared.util.ResponseData;
import com.dyc.backendecommerce.user.UserRepository;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {
  private static final String NOT_FOUND = "Order not found";
  private static final String ORDER_NUMBER_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
  private static final DateTimeFormatter ORDER_DATE = DateTimeFormatter.ofPattern("yyMMdd");
  private static final SecureRandom RANDOM = new SecureRandom();

  private final OrderRepository orderRepository;
  private final CartItemRepository cartItemRepository;
  private final ProductVariantRepository productVariantRepository;
  private final UserRepository userRepository;

  /** Turns the user's cart into an order: re-checks stock under row locks, deducts it, empties the cart. */
  // Stock and sales ranking change, so cached storefront products are stale
  @CacheEvict(cacheNames = {CacheConfig.BEST_SELLERS, CacheConfig.STORE_BANNERS}, allEntries = true)
  public OrderResponse placeOrder(UUID userId, PlaceOrderRequest request) {
    // Lock the variants before reading the cart so the stock values used below are current
    List<UUID> variantIds = cartItemRepository.findVariantIdsByUserId(userId);
    if (variantIds.isEmpty()) {
      throw new BadRequestException("Your cart is empty");
    }
    Map<UUID, ProductVariant> variants =
        productVariantRepository.findAllForUpdate(variantIds).stream()
            .collect(Collectors.toMap(ProductVariant::getId, Function.identity()));

    var order =
        Order.builder()
            .orderNumber(newOrderNumber())
            .user(userRepository.getReferenceById(userId))
            .status(OrderStatus.PENDING)
            .paymentMethod(request.getPaymentMethod())
            .fullName(request.getFullName().trim())
            .phone(request.getPhone().trim())
            .email(request.getEmail().trim())
            .address(request.getAddress().trim())
            .city(request.getCity().trim())
            .country(request.getCountry().trim())
            .note(request.getNote() == null || request.getNote().isBlank() ? null : request.getNote().trim())
            .build();

    List<CartItem> cartItems = cartItemRepository.findByUserIdOrderByCreatedAtAsc(userId);
    for (CartItem cartItem : cartItems) {
      var variant = variants.get(cartItem.getProductVariant().getId());
      int quantity = cartItem.getQuantity();
      if (quantity > variant.getStockQty()) {
        throw new BadRequestException(
            variant.getStockQty() == 0
                ? variant.getProduct().getName() + " (" + variant.getName() + ") is sold out. Please remove it from your cart."
                : "Only " + variant.getStockQty() + " left of " + variant.getProduct().getName()
                    + " (" + variant.getName() + "). Please update your cart.");
      }
      variant.setStockQty(variant.getStockQty() - quantity);

      var unitPrice = variant.getSalePrice() != null ? variant.getSalePrice() : variant.getPrice();
      order.getItems().add(
          OrderItem.builder()
              .order(order)
              .productVariant(variant)
              .productId(variant.getProduct().getId())
              .productName(variant.getProduct().getName())
              .variantName(variant.getName())
              .imageUuid(ProductImages.firstImage(variant))
              .unitPrice(unitPrice)
              .quantity(quantity)
              .lineTotal(unitPrice.multiply(BigDecimal.valueOf(quantity)))
              .build());
    }

    var subtotal =
        order.getItems().stream().map(OrderItem::getLineTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    var shippingFee = CartService.shippingFeeFor(order.getItems().size());
    order.setSubtotal(subtotal);
    order.setShippingFee(shippingFee);
    order.setTotal(subtotal.add(shippingFee));

    var saved = orderRepository.save(order);
    cartItemRepository.deleteByUserId(userId);
    return toResponse(saved);
  }

  @Transactional(readOnly = true)
  public ResponseData<OrderResponse> getMyOrders(UUID userId, Pageable pageable) {
    Page<Order> orders = orderRepository.findByUserId(userId, pageable);
    return ResponseData.<OrderResponse>builder()
        .data(orders.stream().map(this::toResponse).toList())
        .total(orders.getTotalElements())
        .page(orders.getNumber())
        .pageSize(pageable.getPageSize())
        .build();
  }

  @Transactional(readOnly = true)
  public OrderResponse getMyOrder(UUID userId, UUID orderId) {
    return toResponse(findOwnedOrder(userId, orderId));
  }

  /** Customers may cancel only before the shop confirms; the reserved stock goes back on sale. */
  // Stock and sales ranking change, so cached storefront products are stale
  @CacheEvict(cacheNames = {CacheConfig.BEST_SELLERS, CacheConfig.STORE_BANNERS}, allEntries = true)
  public OrderResponse cancelMyOrder(UUID userId, UUID orderId) {
    var order = findOwnedOrder(userId, orderId);
    if (order.getStatus() != OrderStatus.PENDING) {
      throw new BadRequestException("Only pending orders can be cancelled");
    }

    List<UUID> variantIds =
        order.getItems().stream()
            .filter(item -> item.getProductVariant() != null)
            .map(item -> item.getProductVariant().getId())
            .toList();
    Map<UUID, ProductVariant> variants =
        productVariantRepository.findAllForUpdate(variantIds).stream()
            .collect(Collectors.toMap(ProductVariant::getId, Function.identity()));
    order.getItems().stream()
        .filter(item -> item.getProductVariant() != null)
        .forEach(
            item -> {
              var variant = variants.get(item.getProductVariant().getId());
              variant.setStockQty(variant.getStockQty() + item.getQuantity());
            });

    order.setStatus(OrderStatus.CANCELLED);
    return toResponse(order);
  }

  // A missing order and someone else's order look the same to the caller
  private Order findOwnedOrder(UUID userId, UUID orderId) {
    return orderRepository
        .findByIdAndUserId(orderId, userId)
        .orElseThrow(() -> new NotFoundException(NOT_FOUND));
  }

  private String newOrderNumber() {
    var suffix = new StringBuilder();
    for (int i = 0; i < 6; i++) {
      suffix.append(ORDER_NUMBER_CHARS.charAt(RANDOM.nextInt(ORDER_NUMBER_CHARS.length())));
    }
    return "DYC" + LocalDate.now().format(ORDER_DATE) + "-" + suffix;
  }

  private OrderResponse toResponse(Order order) {
    return OrderResponse.builder()
        .id(order.getId())
        .orderNumber(order.getOrderNumber())
        .status(order.getStatus())
        .paymentMethod(order.getPaymentMethod())
        .fullName(order.getFullName())
        .phone(order.getPhone())
        .email(order.getEmail())
        .address(order.getAddress())
        .city(order.getCity())
        .country(order.getCountry())
        .note(order.getNote())
        .subtotal(order.getSubtotal())
        .shippingFee(order.getShippingFee())
        .total(order.getTotal())
        .createdAt(order.getCreatedAt())
        .items(
            order.getItems().stream()
                .map(
                    item ->
                        OrderItemResponse.builder()
                            .id(item.getId())
                            .productId(item.getProductId())
                            .productName(item.getProductName())
                            .variantName(item.getVariantName())
                            .imageUuid(item.getImageUuid())
                            .unitPrice(item.getUnitPrice())
                            .quantity(item.getQuantity())
                            .lineTotal(item.getLineTotal())
                            .build())
                .toList())
        .build();
  }
}
