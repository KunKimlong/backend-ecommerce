package com.dyc.backendecommerce.product;

import java.util.UUID;

import com.dyc.backendecommerce.asset.Asset;
import com.dyc.backendecommerce.asset.AssetRepository;
import com.dyc.backendecommerce.category.CategoryService;
import com.dyc.backendecommerce.optionvalue.OptionValue;
import com.dyc.backendecommerce.optionvalue.OptionValueRepository;
import com.dyc.backendecommerce.product.admin.ProductResponse;
import com.dyc.backendecommerce.product.admin.ProductRequest;
import com.dyc.backendecommerce.product.admin.ProductVariantResponse;
import com.dyc.backendecommerce.product.admin.ProductVariantUpdateRequest;
import com.dyc.backendecommerce.product.store.ProductData;
import com.dyc.backendecommerce.product.store.StoreProductFilter;
import com.dyc.backendecommerce.product.store.StoreProductSort;
import com.dyc.backendecommerce.shared.config.CacheConfig;
import com.dyc.backendecommerce.shared.exception.BadRequestException;
import com.dyc.backendecommerce.shared.exception.NotFoundException;
import com.dyc.backendecommerce.shared.util.ResponseData;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

  @CacheEvict(cacheNames = {CacheConfig.BEST_SELLERS, CacheConfig.STORE_BANNERS}, allEntries = true)
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

  @Transactional(readOnly = true)
  public ResponseData<ProductResponse> getAllProducts(String name, Pageable pageable) {
    Page<Product> products;
    if (name != null && !name.isBlank()) {
      products = productRepository.findByNameContainingIgnoreCase(name, pageable);
    } else {
      products = productRepository.findAll(pageable);
    }
    List<ProductResponse> productDataList =
        withDetails(products).stream().map(product -> modelMapper.map(product, ProductResponse.class)).toList();
    return ResponseData.<ProductResponse>builder()
        .data(productDataList)
        .total(products.getTotalElements())
        .page(products.getNumber())
        .pageSize(pageable.getPageSize())
        .build();
  }

  @Transactional(readOnly = true)
  public ResponseData<ProductData> getNewArrivalProducts(Pageable pageable) {
    var sevenDaysAgo = LocalDateTime.now().minusDays(10);
    Page<Product> products =
        productRepository.findByCreatedAtAfterOrderByCreatedAtDesc(sevenDaysAgo, pageable);
    List<ProductData> productDataList =
        withDetails(products).stream().map(product -> modelMapper.map(product, ProductData.class)).toList();
    return ResponseData.<ProductData>builder()
        .data(productDataList)
        .total(products.getTotalElements())
        .page(products.getNumber())
        .pageSize(pageable.getPageSize())
        .build();
  }

  // Second step of a paged query: load the page's products with their collections, keeping page order.
  // Fetching collections in the paged query itself would make Hibernate page in memory.
  private List<Product> withDetails(Page<Product> page) {
    List<UUID> ids = page.map(Product::getId).getContent();
    Map<UUID, Product> detailed =
        productRepository.findAllByIdIn(ids).stream()
            .collect(Collectors.toMap(Product::getId, Function.identity()));
    return ids.stream().map(detailed::get).toList();
  }

  public ProductResponse getProductById(UUID id) {
    var product =
        productRepository.findById(id).orElseThrow(() -> new NotFoundException(NOT_FOUND));
    return modelMapper.map(product, ProductResponse.class);
  }

  // Page the ids first, then fetch details, so collection fetches don't force in-memory paging
  @Transactional(readOnly = true)
  public ResponseData<ProductData> getStoreProducts(StoreProductFilter filter, Pageable pageable) {
    Page<Product> page =
        productRepository.findAll(
            filter.toSpecification(categoryMatches(filter.namePatterns(false))), pageable);
    List<ProductData> data =
        withDetails(page).stream().map(product -> modelMapper.map(product, ProductData.class)).toList();
    return ResponseData.<ProductData>builder()
        .data(data)
        .total(page.getTotalElements())
        .page(page.getNumber())
        .pageSize(pageable.getPageSize())
        .build();
  }

  /** Type-ahead matches for the storefront search box, best match first. */
  @Transactional(readOnly = true)
  public List<ProductData> getSearchSuggestions(StoreProductFilter filter, int size) {
    Page<Product> page =
        productRepository.findAll(
            filter.toSuggestionSpecification(categoryMatches(filter.namePatterns(true))),
            PageRequest.of(0, size));
    return withDetails(page).stream().map(product -> modelMapper.map(product, ProductData.class)).toList();
  }

  // Categories are a handful of rows, so matching them up front is cheap and keeps the product query
  // on its indexes; insertion order is kept so the generated SQL is the same for the same search
  private Map<String, List<UUID>> categoryMatches(List<String> patterns) {
    Map<String, List<UUID>> matches = new LinkedHashMap<>();
    patterns.forEach(pattern -> matches.put(pattern, categoryService.findIdsByNameLike(pattern)));
    return matches;
  }

  /** Top sellers by units sold; until enough has sold, newest products fill the remaining slots. */
  @Cacheable(CacheConfig.BEST_SELLERS)
  @Transactional(readOnly = true)
  public List<ProductData> getBestSellers(int size) {
    Set<UUID> ids = new LinkedHashSet<>(productRepository.findBestSellingIds(PageRequest.of(0, size)));
    if (ids.size() < size) {
      productRepository
          .findAll(PageRequest.of(0, size, StoreProductSort.NEWEST.toSort()))
          .forEach(product -> ids.add(product.getId()));
    }
    Map<UUID, Product> detailed =
        productRepository.findAllByIdIn(ids).stream()
            .collect(Collectors.toMap(Product::getId, Function.identity()));
    return ids.stream()
        .limit(size)
        .map(id -> modelMapper.map(detailed.get(id), ProductData.class))
        .toList();
  }

  public ProductData getStoreProductById(UUID id) {
    var product =
        productRepository.findById(id).orElseThrow(() -> new NotFoundException(NOT_FOUND));
    return modelMapper.map(product, ProductData.class);
  }

  @CacheEvict(cacheNames = {CacheConfig.BEST_SELLERS, CacheConfig.STORE_BANNERS}, allEntries = true)
  public ProductResponse update(UUID id, ProductRequest request) {
    var product =
        productRepository.findById(id).orElseThrow(() -> new NotFoundException(NOT_FOUND));

    var category = categoryService.getCategory(request.getCategoryId());

    Set<Asset> assets = getAssets(request.getAssetIds());

    product.setName(request.getName());
    product.setDescription(request.getDescription());
    product.setSalePrice(request.getSalePrice());
    product.setCategory(category);
    product.setAssets(assets);

    Map<UUID, ProductVariant> existingVariants = Map.of();
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

  @CacheEvict(cacheNames = {CacheConfig.BEST_SELLERS, CacheConfig.STORE_BANNERS}, allEntries = true)
  public void delete(UUID id) {
    if (!productRepository.existsById(id)) {
      throw new NotFoundException(NOT_FOUND);
    }
    productRepository.deleteById(id);
  }

  public ProductVariantResponse getVariantById(UUID variantId) {
    var variant =
        productVariantRepository
            .findWithDetailsById(variantId)
            .orElseThrow(() -> new NotFoundException(VARIANT_NOT_FOUND));
    return modelMapper.map(variant, ProductVariantResponse.class);
  }

  @CacheEvict(cacheNames = {CacheConfig.BEST_SELLERS, CacheConfig.STORE_BANNERS}, allEntries = true)
  public ProductVariantResponse updateVariant(
      UUID variantId, ProductVariantUpdateRequest request) {
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

  private Set<OptionValue> getOptionValues(Collection<UUID> optionValueIds) {
    if (optionValueIds == null || optionValueIds.isEmpty()) {
      return new HashSet<>();
    }

    Set<OptionValue> optionValues = new HashSet<>(optionValueRepository.findAllById(optionValueIds));
    if (optionValues.size() != new HashSet<>(optionValueIds).size()) {
      throw new BadRequestException("One or more variant option values were not found");
    }
    return optionValues;
  }

  private Set<Asset> getAssets(Collection<UUID> assetIds) {
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
