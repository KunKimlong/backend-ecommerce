package com.dyc.backendecommerce.asset;

import com.dyc.backendecommerce.shared.service.StorageService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@AllArgsConstructor
@Service
@Transactional
public class AssetService {
  private final StorageService storageService;
  private final AssetRepository assetRepository;

  public void save(MultipartFile file) throws IOException {
    String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
    Path path = storageService.getFilePath("image", fileName);
    var asset = Asset.builder().name(fileName).path(path.toString()).build();
    assetRepository.save(asset);
    Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
  }
}
