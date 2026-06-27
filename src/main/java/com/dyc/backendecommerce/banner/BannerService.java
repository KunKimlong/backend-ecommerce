package com.dyc.backendecommerce.banner;

import com.dyc.backendecommerce.asset.Asset;
import com.dyc.backendecommerce.asset.AssetData;
import com.dyc.backendecommerce.asset.AssetRepository;
import com.dyc.backendecommerce.asset.AssetService;
import com.dyc.backendecommerce.banner.admin.BannerRequest;
import com.dyc.backendecommerce.banner.admin.BannerResponse;
import com.dyc.backendecommerce.banner_type.BannerTypeRepository;
import com.dyc.backendecommerce.product.ProductRepository;
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

@Service
@AllArgsConstructor
@Transactional
public class BannerService {
  public static final String NOT_FOUND_MESSAGE = "Banner not found";

  private final BannerRepository bannerRepository;
  private final ProductRepository productRepository;
  private final AssetRepository assetRepository;
  private final ModelMapper modelMapper;
  private final BannerTypeRepository bannerTypeRepository;
  private final AssetService assetService;

  @Transactional(readOnly = true)
  public ResponseData<BannerResponse> getAllBanners(String label, Pageable pageable) {
    Page<Banner> banners;
    if (label != null && !label.isBlank()) {
      banners = bannerRepository.findByLabelContainingIgnoreCase(label, pageable);
    } else {
      banners = bannerRepository.findAll(pageable);
    }
    List<BannerResponse> bannerResponses =
        banners.getContent().stream().map(this::mapToBannerResponse).toList();

    return ResponseData.<BannerResponse>builder()
        .data(bannerResponses)
        .page(banners.getNumber())
        .pageSize(banners.getSize())
        .total(banners.getTotalElements())
        .build();
  }

  @Transactional(readOnly = true)
  public BannerResponse getBannerById(Long id) {
    var banner =
        bannerRepository.findById(id).orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE));
    return mapToBannerResponse(banner);
  }

  public BannerResponse createBanner(BannerRequest bannerRequest, MultipartFile file) {
    var banner = new Banner();
    setBannerRelations(banner, bannerRequest);
    modelMapper.map(bannerRequest, banner);

    if (file != null && !file.isEmpty()) {
      banner.setAsset(uploadFileIfPresent(file));
    }

    return mapToBannerResponse(bannerRepository.save(banner));
  }

  public BannerResponse updateBanner(Long id, BannerRequest bannerRequest, MultipartFile file) {
    var banner =
        bannerRepository.findById(id).orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE));

    modelMapper.map(bannerRequest, banner);
    setBannerRelations(banner, bannerRequest);

    if (file != null && !file.isEmpty()) {
      if (banner.getAsset() != null) {
        try {
          assetService.delete(banner.getAsset().getUuid());
        } catch (IOException e) {
          throw new InternalServerError("Failed to delete old image");
        }
      }
      banner.setAsset(uploadFileIfPresent(file));
    }

    bannerRepository.save(banner);
    return mapToBannerResponse(banner);
  }

  private void setBannerRelations(Banner banner, BannerRequest bannerRequest) {
    if (bannerRequest.getProductId() != null) {
      banner.setProduct(
          productRepository
              .findById(bannerRequest.getProductId())
              .orElseThrow(() -> new NotFoundException("Product not found")));
    } else {
      banner.setProduct(null);
    }

    if (bannerRequest.getBannerTypeId() != null) {
      banner.setBannerType(
          bannerTypeRepository
              .findById(bannerRequest.getBannerTypeId())
              .orElseThrow(() -> new NotFoundException("Banner type not found")));
    } else {
      banner.setBannerType(null);
    }

    if (bannerRequest.getAssetId() != null) {
      banner.setAsset(
          assetRepository
              .findById(bannerRequest.getAssetId())
              .orElseThrow(() -> new NotFoundException("Asset not found")));
    }
  }

  private Asset uploadFileIfPresent(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      return null;
    }
    try {
      var assetData = assetService.save(file, AssetType.BANNER);
      return assetRepository.findById(assetData.getId()).orElse(null);
    } catch (IOException e) {
      throw new InternalServerError("Failed to upload image");
    }
  }

  public void deleteBanner(Long id) {
    if (!bannerRepository.existsById(id)) {
      throw new NotFoundException(NOT_FOUND_MESSAGE);
    }
    bannerRepository.deleteById(id);
  }

  private BannerResponse mapToBannerResponse(Banner banner) {
    BannerResponse response = modelMapper.map(banner, BannerResponse.class);
    if (banner.getBannerType() != null) {
      response.setBannerTypeId(banner.getBannerType().getId());
      response.setType(banner.getBannerType().getName());
    }
    if (banner.getAsset() != null) {
      var assetData = modelMapper.map(banner.getAsset(), AssetData.class);
      response.setAsset(assetData);
    }
    return response;
  }
}
