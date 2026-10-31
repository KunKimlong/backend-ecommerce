package com.dyc.backendecommerce.option;

import java.util.UUID;

import com.dyc.backendecommerce.shared.util.ResponseData;
import lombok.AllArgsConstructor;
import com.dyc.backendecommerce.shared.util.PageRequests;
import java.util.Set;
import org.springframework.data.domain.Pageable;
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
@RequestMapping("/api/option")
@AllArgsConstructor
public class OptionController {
  // Columns the admin table can sort by
  private static final Set<String> SORTABLE = Set.of("id", "name");

  private final OptionService optionService;

  @GetMapping
  public ResponseEntity<ResponseData<OptionResponse>> getOptions(
      @RequestParam(required = false) String name,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "5") int size,
      @RequestParam(defaultValue = "id") String sortBy,
      @RequestParam(defaultValue = "false") boolean ascending) {
    Pageable pageable = PageRequests.sorted(page, size, sortBy, ascending, SORTABLE);
    var optionsResponse = optionService.getAllOptions(name, pageable);
    return new ResponseEntity<>(optionsResponse, HttpStatus.OK);
  }

  @PostMapping
  public ResponseEntity<OptionResponse> createOption(
      @RequestBody OptionRequest optionRequest) {
    var optionResponse = optionService.saveOption(optionRequest);
    return new ResponseEntity<>(optionResponse, HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public ResponseEntity<OptionResponse> updateOption(
      @PathVariable UUID id, @RequestBody OptionRequest optionRequest) {
    var optionResponse = optionService.updateOption(id, optionRequest);
    return new ResponseEntity<>(optionResponse, HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteOption(@PathVariable UUID id) {
    optionService.deleteOption(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
