package com.dyc.backendecommerce.product;

import com.dyc.backendecommerce.asset.Asset;
import com.dyc.backendecommerce.asset.AssetRepository;
import com.dyc.backendecommerce.category.CategoryService;
import com.dyc.backendecommerce.option.OptionValue;
import com.dyc.backendecommerce.option.OptionValueRepository;
import com.dyc.backendecommerce.product.admin.ProductResponse;
import com.dyc.backendecommerce.product.admin.ProductRequest;
import com.dyc.backendecommerce.product.admin.ProductVariantResponse;
import com.dyc.backendecommerce.product.admin.ProductVariantUpdateRequest;
import com.dyc.backendecommerce.product.store.ProductData;
import com.dyc.backendecommerce.shared.exception.BadRequestException;
import com.dyc.backendecommerce.shared.exception.NotFoundException;
import com.dyc.backendecommerce.shared.util.ResponseData;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
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
  private final ProductVariantRepository productVariantRepository;
  private final OptionValueRepository optionValueRepository;
  private final AssetRepository assetRepository;
  private final ModelMapper modelMapper;
  private final CategoryService categoryService;
  private static final String NOT_FOUND = "Product not found";
  private static final String VARIANT_NOT_FOUND = "Variant not found";

  public ProductResponse save(ProductRequest request) {
    var category = categoryService.getCategory(request.getCategoryId());

    Set<Asset> assets = getAssets(request.getAssetIds());

    var product =
        Product.builder()
            .name(request.getName())
            .description(request.getDescription())
            .salePrice(request.getSalePrice())
            .category(category)
            .assets(assets)
            .build();

    Set<ProductVariant> variants = new HashSet<>();
    if (request.getVariants() != null) {
      for (var vr : request.getVariants()) {
        Set<OptionValue> optionValues = getOptionValues(vr.getOptionValueIds());
        Set<Asset> variantAssets = getAssets(vr.getAssetIds());
        var variant =
            ProductVariant.builder()
                .name(vr.getName())
                .price(vr.getPrice())
                .salePrice(vr.getSalePrice())
                .stockQty(vr.getStockQty())
                .optionValues(optionValues)
                .assets(variantAssets)
                .product(product)
                .build();
        variants.add(variant);
      }
    }
    product.setVariants(variants);

    return modelMapper.map(productRepository.save(product), ProductResponse.class);
  }

  public ResponseData<ProductResponse> getAllProducts(String name, Pageable pageable) {
    Page<Product> products;
    if (name != null && !name.isBlank()) {
      products = productRepository.findByNameContainingIgnoreCase(name, pageable);
    } else {
      products = productRepository.findAll(pageable);
    }
    List<ProductResponse> productDataList =
        products.stream().map(product -> modelMapper.map(product, ProductResponse.class)).toList();
    return ResponseData.<ProductResponse>builder()
        .data(productDataList)
        .total(products.getTotalElements())
        .page(products.getNumber())
        .pageSize(pageable.getPageSize())
        .build();
  }

  public ResponseData<ProductData> getNewArrivalProducts(Pageable pageable) {
    var sevenDaysAgo = LocalDateTime.now().minusDays(10);
    Page<Product> products =
        productRepository.findByCreatedAtAfterOrderByCreatedAtDesc(sevenDaysAgo, pageable);
    List<ProductData> productDataList =
        products.stream().map(product -> modelMapper.map(product, ProductData.class)).toList();
    return ResponseData.<ProductData>builder()
        .data(productDataList)
        .total(products.getTotalElements())
        .page(products.getNumber())
        .pageSize(pageable.getPageSize())
        .build();
  }

  public ProductResponse getProductById(Long id) {
    var product =
        productRepository.findById(id).orElseThrow(() -> new NotFoundException(NOT_FOUND));
    return modelMapper.map(product, ProductResponse.class);
  }

  public ProductResponse update(Long id, ProductRequest request) {
    var product =
        productRepository.findById(id).orElseThrow(() -> new NotFoundException(NOT_FOUND));

    var category = categoryService.getCategory(request.getCategoryId());

    Set<Asset> assets = getAssets(request.getAssetIds());

    product.setName(request.getName());
    product.setDescription(request.getDescription());
    product.setSalePrice(request.getSalePrice());
    product.setCategory(category);
    product.setAssets(assets);

    Map<Long, ProductVariant> existingVariants = Map.of();
    if (product.getVariants() != null) {
      existingVariants =
          product.getVariants().stream()
              .filter(v -> v.getId() != null)
              .collect(Collectors.toMap(ProductVariant::getId, Function.identity()));
    }

    Set<ProductVariant> updatedVariants = new HashSet<>();
    if (request.getVariants() != null) {
      for (var vr : request.getVariants()) {
        Set<OptionValue> optionValues = getOptionValues(vr.getOptionValueIds());
        Set<Asset> variantAssets = getAssets(vr.getAssetIds());

        ProductVariant variant;
        if (vr.getId() != null && existingVariants.containsKey(vr.getId())) {
          variant = existingVariants.get(vr.getId());
          variant.setName(vr.getName());
          variant.setPrice(vr.getPrice());
          variant.setSalePrice(vr.getSalePrice());
          variant.setStockQty(vr.getStockQty());
          variant.setOptionValues(optionValues);
          variant.setAssets(variantAssets);
        } else {
          variant =
              ProductVariant.builder()
                  .name(vr.getName())
                  .price(vr.getPrice())
                  .salePrice(vr.getSalePrice())
                  .stockQty(vr.getStockQty())
                  .optionValues(optionValues)
                  .assets(variantAssets)
                  .product(product)
                  .build();
        }
        updatedVariants.add(variant);
      }
    }

    if (product.getVariants() == null) {
      product.setVariants(new HashSet<>());
    }
    product.getVariants().clear();
    product.getVariants().addAll(updatedVariants);

    return modelMapper.map(productRepository.save(product), ProductResponse.class);
  }

  public void delete(Long id) {
    if (!productRepository.existsById(id)) {
      throw new NotFoundException(NOT_FOUND);
    }
    productRepository.deleteById(id);
  }

  public ProductVariantResponse getVariantById(Long variantId) {
    var variant =
        productVariantRepository
            .findWithDetailsById(variantId)
            .orElseThrow(() -> new NotFoundException(VARIANT_NOT_FOUND));
    return modelMapper.map(variant, ProductVariantResponse.class);
  }

  public ProductVariantResponse updateVariant(
      Long variantId, ProductVariantUpdateRequest request) {
    var variant =
        productVariantRepository
            .findWithDetailsById(variantId)
            .orElseThrow(() -> new NotFoundException(VARIANT_NOT_FOUND));

    variant.setName(request.getName());
    variant.setPrice(request.getPrice());
    variant.setSalePrice(request.getSalePrice());
    variant.setStockQty(request.getStockQty());
    variant.setOptionValues(getOptionValues(request.getOptionValueIds()));
    variant.setAssets(getAssets(request.getAssetIds()));

    return modelMapper.map(
        productVariantRepository.save(variant), ProductVariantResponse.class);
  }

  private Set<OptionValue> getOptionValues(Collection<Long> optionValueIds) {
    if (optionValueIds == null || optionValueIds.isEmpty()) {
      return new HashSet<>();
    }

    Set<OptionValue> optionValues = new HashSet<>(optionValueRepository.findAllById(optionValueIds));
    if (optionValues.size() != new HashSet<>(optionValueIds).size()) {
      throw new BadRequestException("One or more variant option values were not found");
    }
    return optionValues;
  }

  private Set<Asset> getAssets(Collection<Long> assetIds) {
    if (assetIds == null || assetIds.isEmpty()) {
      return new HashSet<>();
    }

    Set<Asset> assets = new HashSet<>(assetRepository.findAllById(assetIds));
    if (assets.size() != new HashSet<>(assetIds).size()) {
      throw new BadRequestException("One or more assets were not found");
    }
    return assets;
  }
}
