package com.dyc.backendecommerce.cart;

import java.util.UUID;

import com.dyc.backendecommerce.optionvalue.OptionValueData;
import com.dyc.backendecommerce.product.ProductImages;
import com.dyc.backendecommerce.product.ProductVariant;
import com.dyc.backendecommerce.product.ProductVariantRepository;
import com.dyc.backendecommerce.shared.exception.BadRequestException;
import com.dyc.backendecommerce.shared.exception.NotFoundException;
import com.dyc.backendecommerce.user.UserRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class CartService {
  public static final int MAX_QUANTITY = 99;
  // Flat delivery charge per order; charged only when there is something to ship
  public static final BigDecimal SHIPPING_FEE = new BigDecimal("15.00");
  private static final String ITEM_NOT_FOUND = "Cart item not found";
  private static final String VARIANT_NOT_FOUND = "Variant not found";

  private final CartItemRepository cartItemRepository;
  private final ProductVariantRepository productVariantRepository;
  private final UserRepository userRepository;
  private final ModelMapper modelMapper;

  @Transactional(readOnly = true)
  public CartResponse getCart(UUID userId) {
    List<CartItemResponse> items =
        cartItemRepository.findByUserIdOrderByCreatedAtAsc(userId).stream()
            .map(this::toItemResponse)
            .toList();
    var subtotal =
        items.stream().map(CartItemResponse::getLineTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    var shippingFee = shippingFeeFor(items.size());
    return CartResponse.builder()
        .items(items)
        .totalQuantity(items.stream().mapToInt(CartItemResponse::getQuantity).sum())
        .subtotal(subtotal)
        .shippingFee(shippingFee)
        .total(subtotal.add(shippingFee))
        .build();
  }

  public static BigDecimal shippingFeeFor(int lineCount) {
    return lineCount == 0 ? BigDecimal.ZERO : SHIPPING_FEE;
  }

  public CartResponse addItem(UUID userId, CartItemRequest request) {
    var variant =
        productVariantRepository
            .findById(request.getVariantId())
            .orElseThrow(() -> new NotFoundException(VARIANT_NOT_FOUND));
    var item =
        cartItemRepository
            .findByUserIdAndProductVariantId(userId, variant.getId())
            .orElseGet(
                () ->
                    CartItem.builder()
                        .user(userRepository.getReferenceById(userId))
                        .productVariant(variant)
                        .build());

    int quantity = item.getQuantity() + request.getQuantity();
    if (quantity > MAX_QUANTITY) {
      throw new BadRequestException("You can add at most " + MAX_QUANTITY + " of one item");
    }
    checkStock(variant, quantity);
    item.setQuantity(quantity);
    cartItemRepository.save(item);
    return getCart(userId);
  }

  public CartResponse updateQuantity(UUID userId, UUID itemId, CartQuantityRequest request) {
    var item =
        cartItemRepository
            .findByIdAndUserId(itemId, userId)
            .orElseThrow(() -> new NotFoundException(ITEM_NOT_FOUND));
    checkStock(item.getProductVariant(), request.getQuantity());
    item.setQuantity(request.getQuantity());
    return getCart(userId);
  }

  public CartResponse removeItem(UUID userId, UUID itemId) {
    var item =
        cartItemRepository
            .findByIdAndUserId(itemId, userId)
            .orElseThrow(() -> new NotFoundException(ITEM_NOT_FOUND));
    cartItemRepository.delete(item);
    return getCart(userId);
  }

  public CartResponse clear(UUID userId) {
    cartItemRepository.deleteByUserId(userId);
    return getCart(userId);
  }

  private void checkStock(ProductVariant variant, int quantity) {
    if (quantity > variant.getStockQty()) {
      throw new BadRequestException(
          "Only " + variant.getStockQty() + " left in stock for " + variant.getName());
    }
  }

  private CartItemResponse toItemResponse(CartItem item) {
    var variant = item.getProductVariant();
    var product = variant.getProduct();
    var unitPrice = variant.getSalePrice() != null ? variant.getSalePrice() : variant.getPrice();
    Set<OptionValueData> optionValues =
        variant.getOptionValues().stream()
            .map(value -> modelMapper.map(value, OptionValueData.class))
            .collect(Collectors.toSet());

    return CartItemResponse.builder()
        .id(item.getId())
        .productId(product.getId())
        .productName(product.getName())
        .variantId(variant.getId())
        .variantName(variant.getName())
        .optionValues(optionValues)
        .imageUuid(ProductImages.firstImage(variant))
        .price(variant.getPrice())
        .unitPrice(unitPrice)
        .quantity(item.getQuantity())
        .stockQty(variant.getStockQty())
        .lineTotal(unitPrice.multiply(BigDecimal.valueOf(item.getQuantity())))
        .build();
  }
}
