package com.dyc.backendecommerce.banner.admin;

import java.util.UUID;

import com.dyc.backendecommerce.banner.BannerService;
import com.dyc.backendecommerce.shared.util.ResponseData;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import com.dyc.backendecommerce.shared.util.PageRequests;
import java.util.Set;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@AllArgsConstructor
@RequestMapping("/api/admin/banner")
public class BannerController {
    // Columns the admin table can sort by
    private static final Set<String> SORTABLE = Set.of("id", "label", "headerLabel", "bannerType", "order", "startAt", "endAt");

    private final BannerService bannerService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BannerResponse> saveBanner(
            @Valid @RequestPart("data") BannerRequest bannerRequest,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        return new ResponseEntity<>(bannerService.createBanner(bannerRequest, file), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ResponseData<BannerResponse>> getBanners(
            @RequestParam(required = false) String label,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "false") boolean ascending) {
        Pageable pageable = PageRequests.sorted(page, size, sortBy, ascending, SORTABLE);
        return new ResponseEntity<>(bannerService.getAllBanners(label, pageable), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BannerResponse> findById(@PathVariable UUID id) {
        return new ResponseEntity<>(bannerService.getBannerById(id), HttpStatus.OK);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<BannerResponse> updateBanner(
            @PathVariable UUID id,
            @Valid @RequestPart("data") BannerRequest bannerRequest,
            @RequestPart(value = "file", required = false) MultipartFile file) {
        return new ResponseEntity<>(bannerService.updateBanner(id, bannerRequest, file), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBanner(@PathVariable UUID id) {
        bannerService.deleteBanner(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
