package com.dyc.backendecommerce.category;

import java.util.List;
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
@RequestMapping("/api/category")
@AllArgsConstructor
public class CategoryController {
  private final CategoryService categoryService;

  @GetMapping
  public ResponseEntity<List<CategoryResponse>> getCategory(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "5") int size,
      @RequestParam(defaultValue = "id") String sortBy,
      @RequestParam(defaultValue = "true") boolean ascending) {
    Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
    Pageable pageable = PageRequest.of(page, size, sort);
    List<CategoryResponse> categoriesResponse = categoryService.getAllCategory(pageable);
    return new ResponseEntity<>(categoriesResponse, HttpStatus.OK);
  }

  @PostMapping
  public ResponseEntity<CategoryResponse> createCategory(
      @RequestBody CategoryRequest categoryRequest) {
    CategoryResponse categoryResponse = categoryService.saveCategory(categoryRequest);
    return new ResponseEntity<>(categoryResponse, HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public ResponseEntity<CategoryResponse> updateCategory(
      @PathVariable long id, @RequestBody CategoryRequest categoryRequest) {
    CategoryResponse categoryResponse = categoryService.updateCategory(id, categoryRequest);
    return new ResponseEntity<>(categoryResponse, HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<CategoryResponse> deleteCategory(@PathVariable long id) {
    CategoryResponse categoryResponse = categoryService.deleteCategory(id);
    return new ResponseEntity<>(categoryResponse, HttpStatus.OK);
  }
}
