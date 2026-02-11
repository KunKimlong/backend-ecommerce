package com.dyc.backendecommerce.shared.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StorageService {
  @Value("${app.file-storage-path}")
  private String basePath;

  public Path getFilePath(String directory, String fileName) throws IOException {
    Path path = Paths.get(basePath, directory, fileName);

    // create folders if not exist
    Files.createDirectories(path.getParent());

    return path;
  }
}
