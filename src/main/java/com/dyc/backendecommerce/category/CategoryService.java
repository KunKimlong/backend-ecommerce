package com.dyc.backendecommerce.category;

import com.dyc.backendecommerce.auth.AuthService;
import java.util.List;

import com.dyc.backendecommerce.shared.exception.NotFoundException;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
@AllArgsConstructor
public class CategoryService {
  private static final String NOT_FOUND_MESSAGE = "Category not found in list";

  private final CategoryRepository categoryRepository;
  private final AuthService authService;
  private final ModelMapper modelMapper;

  private Page<Category> getAllCategories(Pageable pageable) {
    return categoryRepository.findAll(pageable);
  }

  @Transactional(readOnly = true)
  public List<CategoryResponse> getAllCategory(Pageable pageable) {
    List<Category> categories = getAllCategories(pageable).getContent();
    return categories.stream()
        .map(category -> modelMapper.map(category, CategoryResponse.class)) // map each element
        .toList();
  }

  public CategoryResponse saveCategory(CategoryRequest categoryRequest) {
    Category category = Category.builder().name(categoryRequest.getName()).build();
    categoryRepository.save(category);
    return modelMapper.map(category, CategoryResponse.class);
  }

  public CategoryResponse updateCategory(Long id, CategoryRequest categoryRequest) {
    Category category = categoryRepository.findById(id).orElse(null);
    if (category != null) {
      category.setName(categoryRequest.getName());
      categoryRepository.save(category);
      return modelMapper.map(category, CategoryResponse.class);
    } else {
      throw new NotFoundException(NOT_FOUND_MESSAGE);
    }
  }

  public CategoryResponse deleteCategory(Long id) {
    Category category = categoryRepository.findById(id).orElse(null);
    if (category != null) {
      categoryRepository.delete(category);
      return modelMapper.map(category, CategoryResponse.class);

    } else {
      throw new NotFoundException(NOT_FOUND_MESSAGE);
    }
  }
}
