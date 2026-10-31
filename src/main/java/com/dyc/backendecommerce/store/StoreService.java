package com.dyc.backendecommerce.store;

import java.util.UUID;

import com.dyc.backendecommerce.asset.Asset;
import com.dyc.backendecommerce.asset.AssetRepository;
import com.dyc.backendecommerce.asset.AssetService;
import com.dyc.backendecommerce.shared.enums.AssetType;
import com.dyc.backendecommerce.shared.exception.InternalServerError;
import com.dyc.backendecommerce.shared.exception.NotFoundException;
import com.dyc.backendecommerce.shared.util.ResponseData;
import com.dyc.backendecommerce.store.admin.StoreRequest;
import com.dyc.backendecommerce.store.admin.StoreResponse;
import com.dyc.backendecommerce.store.store.StoreInfoResponse;
import java.io.IOException;
import java.util.List;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@AllArgsConstructor
public class StoreService {
  private static final String NOT_FOUND_MESSAGE = "Store not found";

  private final StoreRepository storeRepository;
  private final AssetRepository assetRepository;
  private final AssetService assetService;
  private final ModelMapper modelMapper;

  @Transactional(readOnly = true)
  public ResponseData<StoreResponse> getAllStores(Pageable pageable) {
    Page<Store> stores = storeRepository.findAll(pageable);
    List<StoreResponse> responses =
        stores.getContent().stream().map(this::mapToStoreResponse).toList();

    return ResponseData.<StoreResponse>builder()
        .data(responses)
        .total(stores.getTotalElements())
        .page(stores.getNumber())
        .pageSize(stores.getSize())
        .build();
  }

  /** Public storefront details; read from Redis, so the database is only hit after an edit or expiry. */
  @Cacheable(
      cacheNames = StoreCacheConfig.STORE_INFO,
      cacheManager = StoreCacheConfig.REDIS_CACHE_MANAGER,
      key = "'current'")
  @Transactional(readOnly = true)
  public StoreInfoResponse getStoreInfo() {
    return storeRepository.findAll().stream()
        .findFirst()
        .map(
            store ->
                new StoreInfoResponse(
                    store.getName(),
                    store.getDescription(),
                    publicImagePath(store.getAsset()),
                    publicImagePath(store.getDarkLogoAsset()),
                    publicImagePath(store.getFaviconAsset()),
                    store.getLocation(),
                    store.getSupportEmail(),
                    store.getSupportPhoneNumber()))
        .orElse(new StoreInfoResponse(null, null, null, null, null, null, null, null));
  }

  private static String publicImagePath(Asset asset) {
    return asset != null ? "/api/asset/image/" + asset.getUuid() : null;
  }

  @CacheEvict(
      cacheNames = StoreCacheConfig.STORE_INFO,
      cacheManager = StoreCacheConfig.REDIS_CACHE_MANAGER,
      key = "'current'")
  public StoreResponse updateStore(
      UUID id, StoreRequest request, MultipartFile file, MultipartFile darkLogo, MultipartFile favicon) {
    Store store =
        storeRepository.findById(id).orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE));

    if (file != null && !file.isEmpty()) {
      deleteAsset(store.getAsset(), "logo");
      store.setAsset(uploadFileIfPresent(file));
    } else if (request.getAssetId() != null) {
      store.setAsset(
          assetRepository
              .findById(request.getAssetId())
              .orElseThrow(() -> new NotFoundException("Asset not found")));
    }
    store.setName(request.getName());
    store.setDescription(request.getDescription());
    store.setLocation(request.getLocation());
    store.setSupportEmail(request.getSupportEmail());
    store.setSupportPhoneNumber(request.getSupportPhoneNumber());

    if (darkLogo != null && !darkLogo.isEmpty()) {
      deleteAsset(store.getDarkLogoAsset(), "dark logo");
      store.setDarkLogoAsset(uploadFileIfPresent(darkLogo));
    } else if (request.isRemoveDarkLogo()) {
      deleteAsset(store.getDarkLogoAsset(), "dark logo");
      store.setDarkLogoAsset(null);
    }
    if (favicon != null && !favicon.isEmpty()) {
      deleteAsset(store.getFaviconAsset(), "favicon");
      store.setFaviconAsset(uploadFileIfPresent(favicon));
    } else if (request.isRemoveFavicon()) {
      deleteAsset(store.getFaviconAsset(), "favicon");
      store.setFaviconAsset(null);
    }

    storeRepository.save(store);
    return mapToStoreResponse(store);
  }

  private void deleteAsset(Asset asset, String label) {
    if (asset == null) {
      return;
    }
    try {
      assetService.delete(asset.getUuid());
    } catch (IOException e) {
      throw new InternalServerError("Failed to delete old " + label);
    }
  }

  private StoreResponse mapToStoreResponse(Store store) {
    StoreResponse response = modelMapper.map(store, StoreResponse.class);
    if (store.getAsset() != null) {
      response.setAssetId(store.getAsset().getId());
      response.setLogoPath("/image/" + store.getAsset().getUuid());
    }
    // Set explicitly: ModelMapper would otherwise match darkLogoAsset.path (the file's location on disk)
    response.setDarkLogoPath(
        store.getDarkLogoAsset() != null ? "/image/" + store.getDarkLogoAsset().getUuid() : null);
    response.setFaviconPath(
        store.getFaviconAsset() != null ? "/image/" + store.getFaviconAsset().getUuid() : null);
    return response;
  }

  private Asset uploadFileIfPresent(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      return null;
    }
    try {
      var assetData = assetService.save(file, AssetType.STORE);
      return assetRepository.findById(assetData.getId()).orElse(null);
    } catch (IOException e) {
      throw new InternalServerError("Failed to upload logo");
    }
  }
}
