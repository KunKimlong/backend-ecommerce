package com.dyc.backendecommerce.store;

import com.dyc.backendecommerce.asset.Asset;
import com.dyc.backendecommerce.asset.AssetRepository;
import com.dyc.backendecommerce.asset.AssetService;
import com.dyc.backendecommerce.shared.enums.AssetType;
import com.dyc.backendecommerce.shared.exception.InternalServerError;
import com.dyc.backendecommerce.shared.exception.NotFoundException;
import com.dyc.backendecommerce.shared.util.ResponseData;
import com.dyc.backendecommerce.store.admin.StoreRequest;
import com.dyc.backendecommerce.store.admin.StoreResponse;
import java.io.IOException;
import java.util.List;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
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

  public StoreResponse updateStore(Long id, StoreRequest request, MultipartFile file) {
    Store store =
        storeRepository.findById(id).orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE));

    if (file != null && !file.isEmpty()) {
      if (store.getAsset() != null) {
        try {
          assetService.delete(store.getAsset().getUuid());
        } catch (IOException e) {
          throw new InternalServerError("Failed to delete old logo");
        }
      }
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
    store.setIsActive(request.getIsActive());

    storeRepository.save(store);
    return mapToStoreResponse(store);
  }

  private StoreResponse mapToStoreResponse(Store store) {
    StoreResponse response = modelMapper.map(store, StoreResponse.class);
    if (store.getAsset() != null) {
      response.setAssetId(store.getAsset().getId());
      response.setLogoPath("/api/asset/image/" + store.getAsset().getUuid());
    }
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
