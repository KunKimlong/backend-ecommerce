package com.dyc.backendecommerce.product;

import com.dyc.backendecommerce.asset.Asset;
import com.dyc.backendecommerce.asset.AssetRepository;
import com.dyc.backendecommerce.category.CategoryService;
import com.dyc.backendecommerce.color.Color;
import com.dyc.backendecommerce.color.ColorRepository;
import com.dyc.backendecommerce.shared.exception.NotFoundException;
import com.dyc.backendecommerce.shared.util.ResponseData;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class ProductService {
  private final ProductRepository productRepository;
  private final ColorRepository colorRepository;
  private final AssetRepository assetRepository;
  private final ModelMapper modelMapper;
  private final CategoryService categoryService;
  private static final String NOT_FOUND = "Product not found";

  public ProductData save(ProductRequest request) {
    var category = categoryService.getCategory(request.getCategoryId());
    Set<Color> colors = new HashSet<>();
    if (request.getColorIds() != null && !request.getColorIds().isEmpty()) {
      colors = new HashSet<>(colorRepository.findAllById(request.getColorIds()));
    }

    Set<Asset> assets = new HashSet<>();
    if (request.getAssetIds() != null && !request.getAssetIds().isEmpty()) {
      assets = new HashSet<>(assetRepository.findAllById(request.getAssetIds()));
    }
    var product =
        Product.builder()
            .name(request.getName())
            .description(request.getDescription())
            .importPrice(request.getImportPrice())
            .salePrice(request.getSalePrice())
            .stockQty(request.getStockQty())
            .category(category)
            .assets(assets)
            .colors(colors)
            .build();

    return modelMapper.map(productRepository.save(product), ProductData.class);
  }

  public ResponseData<ProductData> getAllProducts(Pageable pageable) {
    Page<Product> products = productRepository.findAll(pageable);
    List<ProductData> productDataList =
        products.stream().map(product -> modelMapper.map(product, ProductData.class)).toList();
    return ResponseData.<ProductData>builder()
        .data(productDataList)
        .total(products.getTotalElements())
        .page(products.getNumber())
        .pageSize(pageable.getPageSize())
        .build();
  }

  public ProductData getProductById(Long id) {
    var product =
        productRepository.findById(id).orElseThrow(() -> new NotFoundException(NOT_FOUND));
    return modelMapper.map(product, ProductData.class);
  }

  public ProductData update(Long id, ProductRequest request) {
    var product =
        productRepository.findById(id).orElseThrow(() -> new NotFoundException(NOT_FOUND));
    Set<Color> colors = new HashSet<>();
    if (request.getColorIds() != null && !request.getColorIds().isEmpty()) {
      colors = new HashSet<>(colorRepository.findAllById(request.getColorIds()));
    }

    var category = categoryService.getCategory(request.getCategoryId());

    Set<Asset> assets = new HashSet<>();
    if (request.getAssetIds() != null && !request.getAssetIds().isEmpty()) {
      assets = new HashSet<>(assetRepository.findAllById(request.getAssetIds()));
    }

    product.setName(request.getName());
    product.setDescription(request.getDescription());
    product.setImportPrice(request.getImportPrice());
    product.setSalePrice(request.getSalePrice());
    product.setStockQty(request.getStockQty());
    product.setCategory(category);
    product.setColors(colors);
    product.setAssets(assets);

    return modelMapper.map(productRepository.save(product), ProductData.class);
  }

  public void delete(Long id) {
    if (!productRepository.existsById(id)) {
      throw new NotFoundException(NOT_FOUND);
    }
    productRepository.deleteById(id);
  }
}
