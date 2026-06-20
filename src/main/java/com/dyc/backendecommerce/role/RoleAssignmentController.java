package com.dyc.backendecommerce.role;

import com.dyc.backendecommerce.shared.exception.NotFoundException;
import com.dyc.backendecommerce.user.UserRepository;
import java.util.HashSet;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/users")
@AllArgsConstructor
public class RoleAssignmentController {

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;

  @Transactional(readOnly = true)
  @GetMapping("/{userId}/roles")
  public ResponseEntity<List<RoleData>> getUserRoles(@PathVariable Long userId) {
    var user =
        userRepository
            .findWithRolesById(userId)
            .orElseThrow(() -> new NotFoundException("User not found"));

    List<RoleData> roleData =
        user.getRoles().stream()
            .map(
                r ->
                    RoleData.builder()
                        .id(r.getId())
                        .name(r.getName())
                        .description(r.getDescription())
                        .build())
            .toList();

    return new ResponseEntity<>(roleData, HttpStatus.OK);
  }

  @Transactional
  @PutMapping("/{userId}/roles")
  public ResponseEntity<List<RoleData>> assignUserRoles(
      @PathVariable Long userId, @RequestBody List<Long> roleIds) {
    var user =
        userRepository
            .findWithRolesById(userId)
            .orElseThrow(() -> new NotFoundException("User not found"));

    var roles = roleRepository.findAllById(roleIds);
    user.setRoles(new HashSet<>(roles));
    userRepository.save(user);

    List<RoleData> roleData =
        user.getRoles().stream()
            .map(
                r ->
                    RoleData.builder()
                        .id(r.getId())
                        .name(r.getName())
                        .description(r.getDescription())
                        .build())
            .toList();

    return new ResponseEntity<>(roleData, HttpStatus.OK);
  }
}
