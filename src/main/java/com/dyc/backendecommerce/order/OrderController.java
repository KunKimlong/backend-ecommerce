package com.dyc.backendecommerce.order;

import java.util.UUID;

import com.dyc.backendecommerce.shared.entity.CustomUserDetails;
import com.dyc.backendecommerce.shared.util.ResponseData;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// Customer's own orders: any signed-in user, every call scoped to the token's user id
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
  private static final int MAX_PAGE_SIZE = 50;
  private final OrderService orderService;

  @PostMapping
  public ResponseEntity<OrderResponse> placeOrder(
      @AuthenticationPrincipal CustomUserDetails user, @Valid @RequestBody PlaceOrderRequest request) {
    return new ResponseEntity<>(orderService.placeOrder(user.getId(), request), HttpStatus.CREATED);
  }

  @GetMapping
  public ResponseEntity<ResponseData<OrderResponse>> getMyOrders(
      @AuthenticationPrincipal CustomUserDetails user,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    var pageable =
        PageRequest.of(
            Math.max(page, 0),
            Math.min(Math.max(size, 1), MAX_PAGE_SIZE),
            Sort.by(Sort.Direction.DESC, "createdAt"));
    return new ResponseEntity<>(orderService.getMyOrders(user.getId(), pageable), HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity<OrderResponse> getMyOrder(
      @AuthenticationPrincipal CustomUserDetails user, @PathVariable UUID id) {
    return new ResponseEntity<>(orderService.getMyOrder(user.getId(), id), HttpStatus.OK);
  }

  @PostMapping("/{id}/cancel")
  public ResponseEntity<OrderResponse> cancelMyOrder(
      @AuthenticationPrincipal CustomUserDetails user, @PathVariable UUID id) {
    return new ResponseEntity<>(orderService.cancelMyOrder(user.getId(), id), HttpStatus.OK);
  }
}
