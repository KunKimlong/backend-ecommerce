package com.dyc.backendecommerce.user;

import java.util.UUID;

import com.dyc.backendecommerce.shared.util.ResponseData;
import jakarta.validation.Valid;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import com.dyc.backendecommerce.shared.util.PageRequests;
import java.util.Set;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {
  // Columns the admin table can sort by
  private static final Set<String> SORTABLE = Set.of("id", "firstName", "email", "phone", "gender", "joinDate", "isActive");

  private final UserService userService;

  @GetMapping
  public ResponseEntity<ResponseData<UserResponse>> getUsers(
      @RequestParam(required = false) String name,
      @RequestParam(required = false) LocalDate joinDate,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "5") int size,
      @RequestParam(defaultValue = "id") String sortBy,
      @RequestParam(defaultValue = "false") boolean ascending) {
    Pageable pageable = PageRequests.sorted(page, size, sortBy, ascending, SORTABLE);
    return new ResponseEntity<>(userService.getAllUsers(name, joinDate, pageable), HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
    return new ResponseEntity<>(userService.getUserById(id), HttpStatus.OK);
  }

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<UserResponse> createUser(
      @RequestPart("data") @Valid UserRequest request,
      @RequestPart(value = "file", required = false) MultipartFile file) {
    return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(request, file));
  }

  @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<UserResponse> updateUser(
      @PathVariable UUID id,
      @RequestPart("data") UserRequest request,
      @RequestPart(value = "file", required = false) MultipartFile file) {
    return ResponseEntity.ok(userService.updateUser(id, request, file));
  }
}
