package com.dyc.backendecommerce.wishlist;

import java.util.UUID;

import com.dyc.backendecommerce.product.ProductRepository;
import com.dyc.backendecommerce.product.store.ProductData;
import com.dyc.backendecommerce.shared.exception.NotFoundException;
import com.dyc.backendecommerce.user.UserRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class WishlistService {
  private final WishlistItemRepository wishlistItemRepository;
  private final ProductRepository productRepository;
  private final UserRepository userRepository;
  private final ModelMapper modelMapper;

  @Transactional(readOnly = true)
  public List<ProductData> getWishlist(UUID userId) {
    return wishlistItemRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
        .map(item -> modelMapper.map(item.getProduct(), ProductData.class))
        .toList();
  }

  // Adding something already saved is a no-op, so double clicks are harmless
  public List<ProductData> add(UUID userId, UUID productId) {
    if (!productRepository.existsById(productId)) {
      throw new NotFoundException("Product not found");
    }
    if (!wishlistItemRepository.existsByUserIdAndProductId(userId, productId)) {
      wishlistItemRepository.save(
          WishlistItem.builder()
              .user(userRepository.getReferenceById(userId))
              .product(productRepository.getReferenceById(productId))
              .build());
    }
    return getWishlist(userId);
  }

  public List<ProductData> remove(UUID userId, UUID productId) {
    wishlistItemRepository.deleteByUserIdAndProductId(userId, productId);
    return getWishlist(userId);
  }
}
