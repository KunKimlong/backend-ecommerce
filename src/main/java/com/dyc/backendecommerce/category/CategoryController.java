package com.dyc.backendecommerce.category;

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
  public ResponseEntity<CategoryResponse> getCategory(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "5") int size,
      @RequestParam(defaultValue = "id") String sortBy,
      @RequestParam(defaultValue = "false") boolean ascending) {
    Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
    Pageable pageable = PageRequest.of(page, size, sort);
    var categoryResponse = categoryService.getAllCategory(pageable);
    return new ResponseEntity<>(categoryResponse, HttpStatus.OK);
  }

  @PostMapping
  public ResponseEntity<CategoryData> createCategory(@RequestBody CategoryRequest categoryRequest) {
    var categoryResponse = categoryService.saveCategory(categoryRequest);
    return new ResponseEntity<>(categoryResponse, HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public ResponseEntity<CategoryData> updateCategory(
      @PathVariable long id, @RequestBody CategoryRequest categoryRequest) {
    var categoryResponse = categoryService.updateCategory(id, categoryRequest);
    return new ResponseEntity<>(categoryResponse, HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteCategory(@PathVariable long id) {
    categoryService.deleteCategory(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
