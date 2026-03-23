package com.dyc.backendecommerce.banner.admin;

import com.dyc.backendecommerce.banner.BannerService;
import com.dyc.backendecommerce.shared.util.ResponseData;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/admin/banner")
public class BannerController {
    private final BannerService bannerService;

    @PostMapping
    public ResponseEntity<BannerResponse> saveBanner(@RequestBody BannerRequest bannerRequest) {
        return new ResponseEntity<>(bannerService.createBanner(bannerRequest), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<ResponseData<BannerResponse>> getBanners(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "false") boolean ascending) {
        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return new ResponseEntity<>(bannerService.getAllBanners(pageable), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BannerResponse> findById(@PathVariable Long id) {
        return new ResponseEntity<>(bannerService.getBannerById(id), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BannerResponse> updateBanner(
            @PathVariable Long id, @RequestBody BannerRequest bannerRequest) {
        return new ResponseEntity<>(bannerService.updateBanner(id, bannerRequest), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBanner(@PathVariable Long id) {
        bannerService.deleteBanner(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
