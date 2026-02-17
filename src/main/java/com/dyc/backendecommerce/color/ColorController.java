package com.dyc.backendecommerce.color;

import jakarta.validation.Valid;
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

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/color")
public class ColorController {
  private final ColorService colorService;

  @GetMapping
  public ResponseEntity<ColorResponse> getColor(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "5") int size,
      @RequestParam(defaultValue = "id") String sortBy,
      @RequestParam(defaultValue = "true") boolean ascending) {
    Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
    Pageable pageable = PageRequest.of(page, size, sort);
    return new ResponseEntity<>(colorService.getAllColor(pageable), HttpStatus.OK);
  }

  @PostMapping
  public ResponseEntity<ColorData> createColor(@Valid @RequestBody ColorRequest colorRequest) {
    return new ResponseEntity<>(colorService.saveColor(colorRequest), HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public ResponseEntity<ColorData> updateColor(
      @PathVariable Long id, @Valid @RequestBody ColorRequest colorRequest) {
    return new ResponseEntity<>(colorService.updateColor(id, colorRequest), HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ColorData> deleteColor(@PathVariable Long id) {
    return new ResponseEntity<>(colorService.deleteColor(id), HttpStatus.OK);
  }
}
