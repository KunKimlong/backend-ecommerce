package com.dyc.backendecommerce.category;

import com.dyc.backendecommerce.shared.exception.NotFoundException;
import java.util.List;

import com.dyc.backendecommerce.shared.util.ResponseData;
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
  public static final String NOT_FOUND_MESSAGE = "Category not found in list";

  private final CategoryRepository categoryRepository;
  private final ModelMapper modelMapper;

  private Page<Category> getAllCategories(Pageable pageable) {
    return categoryRepository.findAll(pageable);
  }

  @Transactional(readOnly = true)
  public ResponseData<CategoryResponse> getAllCategory(Pageable pageable) {
    Page<Category> categories = getAllCategories(pageable);
    List<CategoryResponse> categoryData =
        categories.getContent().stream()
            .map(category -> modelMapper.map(category, CategoryResponse.class))
            .toList();

    return ResponseData.<CategoryResponse>builder()
        .data(categoryData)
        .page(categories.getNumber())
        .pageSize(categories.getSize())
        .total(categories.getTotalElements())
        .build();
  }

  public CategoryResponse saveCategory(CategoryRequest categoryRequest) {
    var category = Category.builder().name(categoryRequest.getName()).build();
    categoryRepository.save(category);
    return modelMapper.map(category, CategoryResponse.class);
  }

  public CategoryResponse updateCategory(Long id, CategoryRequest categoryRequest) {
    var category = categoryRepository.findById(id).orElse(null);
    if (category != null) {
      category.setName(categoryRequest.getName());
      categoryRepository.save(category);
      return modelMapper.map(category, CategoryResponse.class);
    } else {
      throw new NotFoundException(NOT_FOUND_MESSAGE);
    }
  }

  public void deleteCategory(Long id) {
    var category = categoryRepository.findById(id).orElse(null);
    if (category != null) {
      categoryRepository.delete(category);

    } else {
      throw new NotFoundException(NOT_FOUND_MESSAGE);
    }
  }

  public Category getCategory(Long id) {
    return categoryRepository
        .findById(id)
        .orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE));
  }
}
