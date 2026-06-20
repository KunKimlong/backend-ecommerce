package com.dyc.backendecommerce.role;

import com.dyc.backendecommerce.shared.util.ResponseData;
import java.util.List;
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
@RequestMapping("/api/admin/roles")
@AllArgsConstructor
public class RoleController {

  private final RoleService roleService;

  @GetMapping
  public ResponseEntity<ResponseData<RoleResponse>> getRoles(
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size,
      @RequestParam(defaultValue = "id") String sortBy,
      @RequestParam(defaultValue = "false") boolean ascending) {
    Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
    Pageable pageable = PageRequest.of(page, size, sort);
    return new ResponseEntity<>(roleService.getAllRoles(pageable), HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity<RoleResponse> getRoleById(@PathVariable Long id) {
    return new ResponseEntity<>(roleService.getRoleById(id), HttpStatus.OK);
  }

  @PostMapping
  public ResponseEntity<RoleResponse> createRole(@RequestBody RoleRequest request) {
    return new ResponseEntity<>(roleService.createRole(request), HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public ResponseEntity<RoleResponse> updateRole(
      @PathVariable Long id, @RequestBody RoleRequest request) {
    return new ResponseEntity<>(roleService.updateRole(id, request), HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
    roleService.deleteRole(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PutMapping("/{id}/permissions")
  public ResponseEntity<RoleResponse> assignPermissions(
      @PathVariable Long id, @RequestBody List<Long> permissionIds) {
    return new ResponseEntity<>(roleService.assignPermissions(id, permissionIds), HttpStatus.OK);
  }
}
