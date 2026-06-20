package com.dyc.backendecommerce.product.admin;

import com.dyc.backendecommerce.product.ProductService;
import com.dyc.backendecommerce.shared.util.ResponseData;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/admin/product")
public class ProductController {
  private final ProductService productService;

  @PostMapping
  public ResponseEntity<ProductResponse> saveProduct(@Valid @RequestBody ProductRequest productRequest) {
    return new ResponseEntity<>(productService.save(productRequest), HttpStatus.CREATED);
  }

  @GetMapping
  public ResponseEntity<ResponseData<ProductResponse>> getProduct(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "5") int size,
      @RequestParam(defaultValue = "id") String sortBy,
      @RequestParam(defaultValue = "false") boolean ascending) {
    Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
    Pageable pageable = PageRequest.of(page, size, sort);
    return new ResponseEntity<>(productService.getAllProducts(pageable), HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity<ProductResponse> findById(@PathVariable Long id) {
    return new ResponseEntity<>(productService.getProductById(id), HttpStatus.OK);
  }

  @PutMapping("/{id}")
  public ResponseEntity<ProductResponse> updateProduct(
      @PathVariable Long id, @Valid @RequestBody ProductRequest productRequest) {
    return new ResponseEntity<>(productService.update(id, productRequest), HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
    productService.delete(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
