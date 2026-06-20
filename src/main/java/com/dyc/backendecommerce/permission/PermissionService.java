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
@Transactional
public class PermissionService {

  public static final String NOT_FOUND_MESSAGE = "Permission not found";

  private final PermissionRepository permissionRepository;
  private final ModelMapper modelMapper;

  public ResponseData<PermissionResponse> getAllPermissions(Pageable pageable) {
    Page<Permission> permissions = permissionRepository.findAll(pageable);
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

  public PermissionResponse createPermission(PermissionRequest request) {
    Permission permission =
        Permission.builder()
            .name(request.getName())
            .description(request.getDescription())
            .module(request.getModule())
            .build();
    return modelMapper.map(permissionRepository.save(permission), PermissionResponse.class);
  }

  public PermissionResponse updatePermission(Long id, PermissionRequest request) {
    Permission permission =
        permissionRepository
            .findById(id)
            .orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE));

    permission.setName(request.getName());
    permission.setDescription(request.getDescription());
    permission.setModule(request.getModule());

    return modelMapper.map(permissionRepository.save(permission), PermissionResponse.class);
  }

  public void deletePermission(Long id) {
    if (!permissionRepository.existsById(id)) {
      throw new NotFoundException(NOT_FOUND_MESSAGE);
    }
    permissionRepository.deleteById(id);
  }
}
