package com.dyc.backendecommerce.banner;

import com.dyc.backendecommerce.asset.AssetRepository;
import com.dyc.backendecommerce.banner.admin.BannerRequest;
import com.dyc.backendecommerce.banner.admin.BannerResponse;
import com.dyc.backendecommerce.product.ProductRepository;
import com.dyc.backendecommerce.shared.exception.NotFoundException;
import com.dyc.backendecommerce.shared.util.ResponseData;
import java.util.List;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional
public class BannerService {
  public static final String NOT_FOUND_MESSAGE = "Banner not found";

  private final BannerRepository bannerRepository;
  private final ProductRepository productRepository;
  private final AssetRepository assetRepository;
  private final ModelMapper modelMapper;

  @Transactional(readOnly = true)
  public ResponseData<BannerResponse> getAllBanners(Pageable pageable) {
    Page<Banner> banners = bannerRepository.findAll(pageable);
    List<BannerResponse> bannerResponses =
        banners.getContent().stream()
            .map(banner -> modelMapper.map(banner, BannerResponse.class))
            .toList();

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
    return modelMapper.map(banner, BannerResponse.class);
  }

  public BannerResponse createBanner(BannerRequest bannerRequest) {
    var banner =
        Banner.builder()
            .label(bannerRequest.getLabel())
            .description(bannerRequest.getDescription())
            .headerLabel(bannerRequest.getHeaderLabel())
            .type(bannerRequest.getType())
            .buttonName(bannerRequest.getButtonName())
            .startAt(bannerRequest.getStartAt())
            .endAt(bannerRequest.getEndAt())
            .build();

    if (bannerRequest.getProductId() != null) {
      banner.setProduct(
          productRepository
              .findById(bannerRequest.getProductId())
              .orElseThrow(() -> new NotFoundException("Product not found")));
    }

    if (bannerRequest.getAssetId() != null) {
      banner.setAsset(
          assetRepository
              .findById(bannerRequest.getAssetId())
              .orElseThrow(() -> new NotFoundException("Asset not found")));
    }

    return modelMapper.map(bannerRepository.save(banner), BannerResponse.class);
  }

  public BannerResponse updateBanner(Long id, BannerRequest bannerRequest) {
    var banner =
        bannerRepository.findById(id).orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE));

    if (bannerRequest.getProductId() != null) {
      banner.setProduct(
          productRepository
              .findById(bannerRequest.getProductId())
              .orElseThrow(() -> new NotFoundException("Product not found")));
    } else {
      banner.setProduct(null);
    }

    if (bannerRequest.getAssetId() != null) {
      banner.setAsset(
          assetRepository
              .findById(bannerRequest.getAssetId())
              .orElseThrow(() -> new NotFoundException("Asset not found")));
    } else {
      banner.setAsset(null);
    }

    banner.setLabel(bannerRequest.getLabel());
    banner.setHeaderLabel(bannerRequest.getHeaderLabel());
    banner.setType(bannerRequest.getType());
    banner.setDescription(bannerRequest.getDescription());
    banner.setButtonName(bannerRequest.getButtonName());
    banner.setStartAt(bannerRequest.getStartAt());
    banner.setEndAt(bannerRequest.getEndAt());

    bannerRepository.save(banner);
    return modelMapper.map(banner, BannerResponse.class);
  }

  public void deleteBanner(Long id) {
    if (!bannerRepository.existsById(id)) {
      throw new NotFoundException(NOT_FOUND_MESSAGE);
    }
    bannerRepository.deleteById(id);
  }
}
