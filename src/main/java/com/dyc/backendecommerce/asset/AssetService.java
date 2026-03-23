package com.dyc.backendecommerce.asset;

import com.dyc.backendecommerce.shared.enums.AssetType;
import com.dyc.backendecommerce.shared.exception.InternalServerError;
import com.dyc.backendecommerce.shared.exception.NotFoundException;
import com.dyc.backendecommerce.shared.service.StorageService;
import com.dyc.backendecommerce.shared.util.ResponseData;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@Service
@Transactional
public class AssetService {
  private final StorageService storageService;
  private final AssetRepository assetRepository;
  private final ModelMapper modelMapper;

  public AssetResponse save(MultipartFile file, AssetType assetType) throws IOException {
    UUID uuid = UUID.randomUUID();
    if (file.getOriginalFilename() == null) {
      throw new InternalServerError("Error uploading file");
    }
    String fileName = uuid + "." + getExtension(file.getOriginalFilename());
    Path path = storageService.getFilePath("image", fileName);
    var asset =
        Asset.builder()
            .name(file.getOriginalFilename())
            .path(path.toString())
            .type(file.getContentType())
            .size(file.getSize())
            .uuid(uuid)
            .assetType(assetType)
            .build();
    assetRepository.save(asset);
    Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
    var assetData = modelMapper.map(asset, AssetResponse.class);
    var responsePath = "/image/" + assetData.getUuid().toString();
    assetData.setPath(responsePath);
    return assetData;
  }

  private String getExtension(String filename) {
    return filename.substring(filename.lastIndexOf(".") + 1);
  }

  public Asset findByUUID(UUID uuid) {
    return assetRepository.findByUuid(uuid);
  }

  public ResponseData<AssetResponse> getAssetResponse(Pageable pageable) {
    Page<Asset> assets = assetRepository.findAll(pageable);
    List<AssetResponse> assetDatas =
        assets.getContent().stream()
            .map(asset -> modelMapper.map(asset, AssetResponse.class))
            .peek(assetData -> assetData.setPath("/image/" + assetData.getUuid()))
            .toList();

    return ResponseData.<AssetResponse>builder()
        .data(assetDatas)
        .total(assets.getTotalElements())
        .page(assets.getNumber())
        .pageSize(assets.getSize())
        .build();
  }

  public void delete(UUID uuid) throws IOException {
    Asset asset = assetRepository.findByUuid(uuid);
    if (asset == null) {
      throw new NotFoundException("Asset not found");
    }
    Files.deleteIfExists(Path.of(asset.getPath()));
    assetRepository.delete(asset);
  }
}
