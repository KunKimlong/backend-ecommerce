package com.dyc.backendecommerce.cart;

import java.util.UUID;

import com.dyc.backendecommerce.shared.entity.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Any signed-in user; every call is scoped to the token's user id, never one from the request
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {
  private final CartService cartService;

  @GetMapping
  public ResponseEntity<CartResponse> getCart(@AuthenticationPrincipal CustomUserDetails user) {
    return new ResponseEntity<>(cartService.getCart(user.getId()), HttpStatus.OK);
  }

  @PostMapping("/items")
  public ResponseEntity<CartResponse> addItem(
      @AuthenticationPrincipal CustomUserDetails user, @Valid @RequestBody CartItemRequest request) {
    return new ResponseEntity<>(cartService.addItem(user.getId(), request), HttpStatus.OK);
  }

  @PutMapping("/items/{id}")
  public ResponseEntity<CartResponse> updateQuantity(
      @AuthenticationPrincipal CustomUserDetails user,
      @PathVariable UUID id,
      @Valid @RequestBody CartQuantityRequest request) {
    return new ResponseEntity<>(cartService.updateQuantity(user.getId(), id, request), HttpStatus.OK);
  }

  @DeleteMapping("/items/{id}")
  public ResponseEntity<CartResponse> removeItem(
      @AuthenticationPrincipal CustomUserDetails user, @PathVariable UUID id) {
    return new ResponseEntity<>(cartService.removeItem(user.getId(), id), HttpStatus.OK);
  }

  @DeleteMapping
  public ResponseEntity<CartResponse> clear(@AuthenticationPrincipal CustomUserDetails user) {
    return new ResponseEntity<>(cartService.clear(user.getId()), HttpStatus.OK);
  }
}
