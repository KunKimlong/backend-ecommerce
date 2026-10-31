package com.dyc.backendecommerce.role;

import java.util.UUID;

import com.dyc.backendecommerce.shared.util.ResponseData;
import java.util.List;
import jakarta.validation.Valid;
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
@RequestMapping("/api/admin/roles")
@AllArgsConstructor
public class RoleController {
  // Columns the admin table can sort by
  private static final Set<String> SORTABLE = Set.of("id", "name", "description");

  private final RoleService roleService;

  @GetMapping
  public ResponseEntity<ResponseData<RoleResponse>> getRoles(
      @RequestParam(required = false) String name,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "id") String sortBy,
      @RequestParam(defaultValue = "false") boolean ascending) {
    Pageable pageable = PageRequests.sorted(page, size, sortBy, ascending, SORTABLE);
    return new ResponseEntity<>(roleService.getAllRoles(name, pageable), HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity<RoleResponse> getRoleById(@PathVariable UUID id) {
    return new ResponseEntity<>(roleService.getRoleById(id), HttpStatus.OK);
  }

  @PostMapping
  public ResponseEntity<RoleResponse> createRole(@Valid @RequestBody RoleRequest request) {
    return new ResponseEntity<>(roleService.createRole(request), HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public ResponseEntity<RoleResponse> updateRole(
      @PathVariable UUID id, @Valid @RequestBody RoleRequest request) {
    return new ResponseEntity<>(roleService.updateRole(id, request), HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteRole(@PathVariable UUID id) {
    roleService.deleteRole(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
