package com.dyc.backendecommerce.wishlist;

import java.util.UUID;

import com.dyc.backendecommerce.product.store.ProductData;
import com.dyc.backendecommerce.shared.entity.CustomUserDetails;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Any signed-in user; every call is scoped to the token's user id
@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
public class WishlistController {
  private final WishlistService wishlistService;

  @GetMapping
  public ResponseEntity<List<ProductData>> getWishlist(@AuthenticationPrincipal CustomUserDetails user) {
    return new ResponseEntity<>(wishlistService.getWishlist(user.getId()), HttpStatus.OK);
  }

  @PutMapping("/{productId}")
  public ResponseEntity<List<ProductData>> add(
      @AuthenticationPrincipal CustomUserDetails user, @PathVariable UUID productId) {
    return new ResponseEntity<>(wishlistService.add(user.getId(), productId), HttpStatus.OK);
  }

  @DeleteMapping("/{productId}")
  public ResponseEntity<List<ProductData>> remove(
      @AuthenticationPrincipal CustomUserDetails user, @PathVariable UUID productId) {
    return new ResponseEntity<>(wishlistService.remove(user.getId(), productId), HttpStatus.OK);
  }
}
