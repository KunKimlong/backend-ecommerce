package com.dyc.backendecommerce.product.admin;

import com.dyc.backendecommerce.product.ProductService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/admin/product-variant")
public class ProductVariantController {
  private final ProductService productService;

  @GetMapping("/{id}")
  public ResponseEntity<ProductVariantResponse> getVariant(@PathVariable Long id) {
    return new ResponseEntity<>(productService.getVariantById(id), HttpStatus.OK);
  }

  @PutMapping("/{id}")
  public ResponseEntity<ProductVariantResponse> updateVariant(
      @PathVariable Long id, @Valid @RequestBody ProductVariantUpdateRequest request) {
    return new ResponseEntity<>(productService.updateVariant(id, request), HttpStatus.OK);
  }
}
