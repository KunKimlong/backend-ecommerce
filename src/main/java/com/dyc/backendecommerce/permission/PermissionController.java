package com.dyc.backendecommerce.permission;

import com.dyc.backendecommerce.shared.util.ResponseData;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/permissions")
@AllArgsConstructor
public class PermissionController {

  private final PermissionService permissionService;

  @GetMapping
  public ResponseEntity<ResponseData<PermissionResponse>> getPermissions(
      @RequestParam(required = false) String name,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(defaultValue = "id") String sortBy,
      @RequestParam(defaultValue = "false") boolean ascending) {
    Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
    Pageable pageable = PageRequest.of(page, size, sort);
    return new ResponseEntity<>(permissionService.getAllPermissions(name, pageable), HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity<PermissionResponse> getPermissionById(@PathVariable Long id) {
    return new ResponseEntity<>(permissionService.getPermissionById(id), HttpStatus.OK);
  }
}
