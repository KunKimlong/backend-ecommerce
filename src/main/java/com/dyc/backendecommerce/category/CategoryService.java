package com.dyc.backendecommerce.category;

import com.dyc.backendecommerce.asset.Asset;
import com.dyc.backendecommerce.asset.AssetData;
import com.dyc.backendecommerce.asset.AssetRepository;
import com.dyc.backendecommerce.asset.AssetService;
import com.dyc.backendecommerce.shared.enums.AssetType;
import com.dyc.backendecommerce.shared.exception.InternalServerError;
import com.dyc.backendecommerce.shared.exception.NotFoundException;
import com.dyc.backendecommerce.shared.util.ResponseData;
import java.io.IOException;
import java.util.List;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Transactional
@Service
@AllArgsConstructor
public class CategoryService {
  public static final String NOT_FOUND_MESSAGE = "Category not found in list";

  private final CategoryRepository categoryRepository;
  private final AssetRepository assetRepository;
  private final AssetService assetService;
  private final ModelMapper modelMapper;

  private Page<Category> getAllCategories(Pageable pageable) {
    return categoryRepository.findAll(pageable);
  }

  @Transactional(readOnly = true)
  public ResponseData<CategoryResponse> getAllCategory(String name, Pageable pageable) {
    Page<Category> categories;
    if (name != null && !name.isBlank()) {
      categories = categoryRepository.findByNameContainingIgnoreCase(name, pageable);
    } else {
      categories = getAllCategories(pageable);
    }
    List<CategoryResponse> categoryData =
        categories.getContent().stream()
            .map(this::mapToCategoryResponse)
            .toList();

    return ResponseData.<CategoryResponse>builder()
        .data(categoryData)
        .page(categories.getNumber())
        .pageSize(categories.getSize())
        .total(categories.getTotalElements())
        .build();
  }

  @Transactional(readOnly = true)
  public CategoryResponse getCategoryById(Long id) {
    var category = categoryRepository
        .findById(id)
        .orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE));
    return mapToCategoryResponse(category);
  }

  public CategoryResponse saveCategory(CategoryRequest categoryRequest, MultipartFile file) {
    var category = Category.builder().name(categoryRequest.getName()).build();

    if (file != null && !file.isEmpty()) {
      category.setAsset(uploadFileIfPresent(file));
    }

    categoryRepository.save(category);
    return mapToCategoryResponse(category);
  }

  public CategoryResponse updateCategory(Long id, CategoryRequest categoryRequest, MultipartFile file) {
    var category = categoryRepository
        .findById(id)
        .orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE));

    category.setName(categoryRequest.getName());

    if (file != null && !file.isEmpty()) {
      if (category.getAsset() != null) {
        try {
          assetService.delete(category.getAsset().getUuid());
        } catch (IOException e) {
          throw new InternalServerError("Failed to delete old image");
        }
      }
      category.setAsset(uploadFileIfPresent(file));
    }

    categoryRepository.save(category);
    return mapToCategoryResponse(category);
  }

  public void deleteCategory(Long id) {
    var category = categoryRepository.findById(id).orElse(null);
    if (category != null) {
      if (category.getAsset() != null) {
        try {
          assetService.delete(category.getAsset().getUuid());
        } catch (IOException e) {
          throw new InternalServerError("Failed to delete image");
        }
      }
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

  private Asset uploadFileIfPresent(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      return null;
    }
    try {
      var assetData = assetService.save(file, AssetType.CATEGORY);
      return assetRepository.findById(assetData.getId()).orElse(null);
    } catch (IOException e) {
      throw new InternalServerError("Failed to upload image");
    }
  }

  private CategoryResponse mapToCategoryResponse(Category category) {
    CategoryResponse response = modelMapper.map(category, CategoryResponse.class);
    if (category.getAsset() != null) {
      var assetData = modelMapper.map(category.getAsset(), AssetData.class);
      response.setAsset(assetData);
    }
    return response;
  }
}