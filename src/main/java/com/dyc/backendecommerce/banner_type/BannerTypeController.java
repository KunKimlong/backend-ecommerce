package com.dyc.backendecommerce.banner_type;

import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
@RequestMapping("/api/banner-type")
public class BannerTypeController {

  private final BannerTypeService bannerTypeService;

  @GetMapping
  public ResponseEntity<List<BannerTypeResponse>> getAll() {
    return new ResponseEntity<>(bannerTypeService.findAll(), HttpStatus.OK);
  }
}
