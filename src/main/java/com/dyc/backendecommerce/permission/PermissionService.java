package com.dyc.backendecommerce.permission;

import com.dyc.backendecommerce.shared.exception.NotFoundException;
import com.dyc.backendecommerce.shared.util.ResponseData;
import java.util.List;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class PermissionService {

  public static final String NOT_FOUND_MESSAGE = "Permission not found";

  private final PermissionRepository permissionRepository;
  private final ModelMapper modelMapper;

  public ResponseData<PermissionResponse> getAllPermissions(String name, Pageable pageable) {
    Page<Permission> permissions;
    if (name != null && !name.isBlank()) {
      permissions = permissionRepository.findByNameContainingIgnoreCase(name, pageable);
    } else {
      permissions = permissionRepository.findAll(pageable);
    }
    List<PermissionResponse> permissionResponses =
        permissions.getContent().stream().map(p -> modelMapper.map(p, PermissionResponse.class)).toList();

    return ResponseData.<PermissionResponse>builder()
        .data(permissionResponses)
        .page(permissions.getNumber())
        .pageSize(permissions.getSize())
        .total(permissions.getTotalElements())
        .build();
  }

  public PermissionResponse getPermissionById(Long id) {
    Permission permission =
        permissionRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE));
    return modelMapper.map(permission, PermissionResponse.class);
  }
}
