package com.dyc.backendecommerce.role;

import com.dyc.backendecommerce.permission.PermissionData;
import com.dyc.backendecommerce.permission.PermissionRepository;
import com.dyc.backendecommerce.shared.exception.NotFoundException;
import com.dyc.backendecommerce.shared.util.ResponseData;
import java.util.Collections;
import java.util.HashSet;
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
public class RoleService {

  public static final String NOT_FOUND_MESSAGE = "Role not found";

  private final RoleRepository roleRepository;
  private final PermissionRepository permissionRepository;
  private final ModelMapper modelMapper;

  public ResponseData<RoleResponse> getAllRoles(String name, Pageable pageable) {
    Page<Role> roles;
    if (name != null && !name.isBlank()) {
      roles = roleRepository.findByNameContainingIgnoreCase(name, pageable);
    } else {
      roles = roleRepository.findAll(pageable);
    }
    List<RoleResponse> roleResponses =
        roles.getContent().stream().map(this::mapToRoleResponse).toList();

    return ResponseData.<RoleResponse>builder()
        .data(roleResponses)
        .page(roles.getNumber())
        .pageSize(roles.getSize())
        .total(roles.getTotalElements())
        .build();
  }

  public RoleResponse getRoleById(Long id) {
    Role role =
        roleRepository.findById(id).orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE));
    return mapToRoleResponse(role);
  }

  public RoleResponse createRole(RoleRequest request) {
    var role = Role.builder().name(request.getName()).description(request.getDescription()).build();

    if (request.getPermissionIds() != null && !request.getPermissionIds().isEmpty()) {
      var permissions = permissionRepository.findByIdIn(request.getPermissionIds());
      role.setPermissions(new HashSet<>(permissions));
    }

    return mapToRoleResponse(roleRepository.save(role));
  }

  public RoleResponse updateRole(Long id, RoleRequest request) {
    var role =
        roleRepository.findById(id).orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE));

    role.setName(request.getName());
    role.setDescription(request.getDescription());

    if (request.getPermissionIds() != null) {
      var permissions = permissionRepository.findByIdIn(request.getPermissionIds());
      role.setPermissions(new HashSet<>(permissions));
    }

    return mapToRoleResponse(roleRepository.save(role));
  }

  public void deleteRole(Long id) {
    if (!roleRepository.existsById(id)) {
      throw new NotFoundException(NOT_FOUND_MESSAGE);
    }
    roleRepository.deleteById(id);
  }

  public RoleResponse assignPermissions(Long roleId, List<Long> permissionIds) {
    var role =
        roleRepository
            .findById(roleId)
            .orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE));

    var permissions = permissionRepository.findByIdIn(permissionIds);
    role.setPermissions(new HashSet<>(permissions));

    return mapToRoleResponse(roleRepository.save(role));
  }

  private RoleResponse mapToRoleResponse(Role role) {
    List<PermissionData> permissionData =
        role.getPermissions() != null
            ? role.getPermissions().stream()
                .map(p -> modelMapper.map(p, PermissionData.class))
                .toList()
            : List.of();

    var response = modelMapper.map(role, RoleResponse.class);
    response.setPermissions(permissionData);
    return response;
  }
}
