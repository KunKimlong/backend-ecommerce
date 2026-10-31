package com.dyc.backendecommerce.permission;

import java.util.UUID;

import com.dyc.backendecommerce.shared.util.ResponseData;
import lombok.AllArgsConstructor;
import com.dyc.backendecommerce.shared.util.PageRequests;
import java.util.Set;
import org.springframework.data.domain.Pageable;
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
  // Columns the admin table can sort by
  private static final Set<String> SORTABLE = Set.of("id", "name", "module", "description");

  private final PermissionService permissionService;

  @GetMapping
  public ResponseEntity<ResponseData<PermissionResponse>> getPermissions(
      @RequestParam(required = false) String name,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(defaultValue = "id") String sortBy,
      @RequestParam(defaultValue = "false") boolean ascending) {
    Pageable pageable = PageRequests.sorted(page, size, sortBy, ascending, SORTABLE);
    return new ResponseEntity<>(permissionService.getAllPermissions(name, pageable), HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity<PermissionResponse> getPermissionById(@PathVariable UUID id) {
    return new ResponseEntity<>(permissionService.getPermissionById(id), HttpStatus.OK);
  }
}
