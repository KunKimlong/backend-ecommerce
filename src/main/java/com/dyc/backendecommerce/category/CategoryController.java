package com.dyc.backendecommerce.category;

import java.util.UUID;

import com.dyc.backendecommerce.shared.util.ResponseData;
import lombok.AllArgsConstructor;
import com.dyc.backendecommerce.shared.util.PageRequests;
import java.util.Set;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/category")
@AllArgsConstructor
public class CategoryController {
  // Columns the admin table can sort by
  private static final Set<String> SORTABLE = Set.of("id", "name");

  private final CategoryService categoryService;

  @GetMapping
  public ResponseEntity<ResponseData<CategoryResponse>> getCategory(
      @RequestParam(required = false) String name,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "5") int size,
      @RequestParam(defaultValue = "id") String sortBy,
      @RequestParam(defaultValue = "false") boolean ascending) {
    Pageable pageable = PageRequests.sorted(page, size, sortBy, ascending, SORTABLE);
    var categoryResponse = categoryService.getAllCategory(name, pageable);
    return new ResponseEntity<>(categoryResponse, HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity<CategoryResponse> findById(@PathVariable UUID id) {
    return new ResponseEntity<>(categoryService.getCategoryById(id), HttpStatus.OK);
  }

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<CategoryResponse> createCategory(
      @RequestPart("data") CategoryRequest categoryRequest,
      @RequestPart(value = "file", required = false) MultipartFile file) {
    var categoryResponse = categoryService.saveCategory(categoryRequest, file);
    return new ResponseEntity<>(categoryResponse, HttpStatus.CREATED);
  }

  @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<CategoryResponse> updateCategory(
      @PathVariable UUID id,
      @RequestPart("data") CategoryRequest categoryRequest,
      @RequestPart(value = "file", required = false) MultipartFile file) {
    var categoryResponse = categoryService.updateCategory(id, categoryRequest, file);
    return new ResponseEntity<>(categoryResponse, HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteCategory(@PathVariable UUID id) {
    categoryService.deleteCategory(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}