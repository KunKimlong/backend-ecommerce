package com.dyc.backendecommerce.dashboard.admin;

import com.dyc.backendecommerce.product.ProductVariantRepository;
import com.dyc.backendecommerce.user.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/dashboard")
@AllArgsConstructor
public class DashboardController {
  private final UserRepository userRepository;
  private final ProductVariantRepository productVariantRepository;

  @GetMapping
  public ResponseEntity<DashboardStatsResponse> getStats() {
    long totalUsers = userRepository.count();
    long totalProductVariants = productVariantRepository.count();
    return ResponseEntity.ok(new DashboardStatsResponse(totalUsers, totalProductVariants));
  }
}
