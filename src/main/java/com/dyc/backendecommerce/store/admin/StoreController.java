package com.dyc.backendecommerce.store.admin;

import com.dyc.backendecommerce.shared.util.ResponseData;
import com.dyc.backendecommerce.store.StoreService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@AllArgsConstructor
@RequestMapping("/api/admin/store")
public class StoreController {
  private final StoreService storeService;
  private final ObjectMapper objectMapper;

  @GetMapping
  public ResponseEntity<ResponseData<StoreResponse>> getStores(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "5") int size,
      @RequestParam(defaultValue = "id") String sortBy,
      @RequestParam(defaultValue = "false") boolean ascending) {
    Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
    Pageable pageable = PageRequest.of(page, size, sort);
    return new ResponseEntity<>(storeService.getAllStores(pageable), HttpStatus.OK);
  }

  @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<StoreResponse> updateStore(
      @PathVariable Long id,
      @RequestPart(value = "data") String dataJson,
      @RequestPart(value = "file", required = false) MultipartFile file) throws Exception {
    StoreRequest request = objectMapper.readValue(dataJson, StoreRequest.class);
    return new ResponseEntity<>(storeService.updateStore(id, request, file), HttpStatus.OK);
  }
}
