package com.dyc.backendecommerce.category;

import com.dyc.backendecommerce.auth.AuthService;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@AllArgsConstructor
public class CategoryService {
  private final CategoryRepository categoryRepository;
  private final AuthService authService;

  public List<Category> getAllCategories() {
    List<Category> categories = categoryRepository.findAll();
    if(categories.isEmpty()){

    }
    return categoryRepository.findAll();
  }

  public Category saveCategory(CategoryRequest categoryRequest) {
    Category category = Category.builder().name(categoryRequest.getName()).build();
    return categoryRepository.save(category);
  }
}
