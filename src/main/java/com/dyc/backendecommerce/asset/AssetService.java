package com.dyc.backendecommerce.asset;

import com.dyc.backendecommerce.shared.enums.AssetType;
import com.dyc.backendecommerce.shared.exception.BadRequestException;
import com.dyc.backendecommerce.shared.exception.InternalServerError;
import com.dyc.backendecommerce.shared.exception.NotFoundException;
import com.dyc.backendecommerce.shared.service.StorageService;
import com.dyc.backendecommerce.shared.util.ResponseData;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
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
    // Type and extension come from the file's bytes, never from the client's name or Content-Type
    ImageType imageType = detectImageType(file);
    String fileName = uuid + "." + imageType.extension();
    Path path = storageService.getFilePath("image", fileName);
    var asset =
        Asset.builder()
            .name(file.getOriginalFilename())
            .path(path.toString())
            .type(imageType.mimeType())
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

  private record ImageType(String mimeType, String extension) {}

  private static final List<String> SERVABLE_IMAGE_TYPES =
      List.of("image/png", "image/jpeg", "image/gif", "image/webp");

  private static ImageType detectImageType(MultipartFile file) throws IOException {
    byte[] header;
    try (InputStream in = file.getInputStream()) {
      header = in.readNBytes(12);
    }
    if (startsWith(header, 0, 0x89, 'P', 'N', 'G')) {
      return new ImageType("image/png", "png");
    }
    if (startsWith(header, 0, 0xFF, 0xD8, 0xFF)) {
      return new ImageType("image/jpeg", "jpg");
    }
    if (startsWith(header, 0, 'G', 'I', 'F', '8')) {
      return new ImageType("image/gif", "gif");
    }
    if (startsWith(header, 0, 'R', 'I', 'F', 'F') && startsWith(header, 8, 'W', 'E', 'B', 'P')) {
      return new ImageType("image/webp", "webp");
    }
    throw new BadRequestException("Only PNG, JPEG, GIF and WEBP images are allowed");
  }

  private static boolean startsWith(byte[] data, int offset, int... expected) {
    if (data.length < offset + expected.length) {
      return false;
    }
    for (int i = 0; i < expected.length; i++) {
      if ((data[offset + i] & 0xFF) != expected[i]) {
        return false;
      }
    }
    return true;
  }

  public Asset findByUUID(UUID uuid) {
    Asset asset = assetRepository.findByUuid(uuid);
    if (asset == null) {
      throw new NotFoundException("Image not found");
    }
    return asset;
  }

  /** Assets stored before type checking may not be images; never serve those inline. */
  public MediaType servableContentType(Asset asset) {
    return SERVABLE_IMAGE_TYPES.contains(asset.getType())
        ? MediaType.parseMediaType(asset.getType())
        : MediaType.APPLICATION_OCTET_STREAM;
  }

  public ResponseData<AssetResponse> getAssetResponse(String name, Pageable pageable) {
    Page<Asset> assets;
    if (name != null && !name.isBlank()) {
      assets = assetRepository.findAllByAssetTypeAndNameContainingIgnoreCase(AssetType.PRODUCT, name, pageable);
    } else {
      assets = assetRepository.findAllByAssetType(AssetType.PRODUCT, pageable);
    }
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
