package com.dyc.backendecommerce.asset;

import com.dyc.backendecommerce.shared.exception.BadRequestException;
import com.dyc.backendecommerce.shared.exception.InternalServerError;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@RestController
@RequestMapping("/api/asset")
public class AssetController {
  private final AssetService assetService;

  @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<AssetData> uploadFile(@RequestParam("file") MultipartFile file) {
    if (file.isEmpty()) {
      throw new BadRequestException("File is required");
    }

    try {
      return new ResponseEntity<>(assetService.save(file), HttpStatus.CREATED);
    } catch (IOException e) {
      throw new InternalServerError("Internal Server Error");
    }
  }

  @GetMapping
  public ResponseEntity<AssetResponse> getAsset(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "5") int size,
      @RequestParam(defaultValue = "id") String sortBy,
      @RequestParam(defaultValue = "false") boolean ascending) {
    Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
    Pageable pageable = PageRequest.of(page, size, sort);
    return new ResponseEntity<>(assetService.getAssetResponse(pageable), HttpStatus.OK);
  }

  @GetMapping("/image/{uuid}")
  public ResponseEntity<Resource> showImage(@PathVariable("uuid") UUID uuid) {
    Asset asset = assetService.findByUUID(uuid);
    Resource resource = new FileSystemResource(asset.getPath());

    return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType(asset.getType()))
        .cacheControl(CacheControl.maxAge(7, TimeUnit.DAYS))
        .body(resource);
  }

  @DeleteMapping("/{uuid}")
  public ResponseEntity<Void> deleteAsset(@PathVariable("uuid") UUID uuid) throws IOException {
    assetService.delete(uuid);
    return ResponseEntity.ok().build();
  }
}
