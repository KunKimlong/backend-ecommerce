package com.dyc.backendecommerce.user;

import com.dyc.backendecommerce.asset.Asset;
import com.dyc.backendecommerce.asset.AssetRepository;
import com.dyc.backendecommerce.asset.AssetService;
import com.dyc.backendecommerce.role.RoleRepository;
import com.dyc.backendecommerce.shared.enums.AssetType;
import com.dyc.backendecommerce.shared.enums.UserRole;
import com.dyc.backendecommerce.shared.exception.InternalServerError;
import com.dyc.backendecommerce.shared.exception.NotFoundException;
import com.dyc.backendecommerce.shared.util.ResponseData;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Transactional
@Service
@AllArgsConstructor
public class UserService {

  public static final String NOT_FOUND_MESSAGE = "User not found";

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final AssetRepository assetRepository;
  private final AssetService assetService;
  private final ModelMapper modelMapper;
  private final PasswordEncoder passwordEncoder;

  public User findByEmail(String email) {
    return userRepository.findByEmail(email).orElse(null);
  }

  public User save(User user) {
    if (user.getPassword() != null && !user.getPassword().startsWith("$2a$")) {
      user.setPassword(passwordEncoder.encode(user.getPassword()));
    }
    return userRepository.save(user);
  }

  @Transactional(readOnly = true)
  public ResponseData<UserResponse> getAllUsers(
      String name, LocalDate joinDate, Pageable pageable) {
    Page<User> users = userRepository.findAll(pageable);
    List<UserResponse> userResponses =
        users.getContent().stream().map(this::mapToUserResponse).toList();

    return ResponseData.<UserResponse>builder()
        .data(userResponses)
        .page(users.getNumber())
        .pageSize(users.getSize())
        .total(users.getTotalElements())
        .build();
  }

  @Transactional(readOnly = true)
  public UserResponse getUserById(Long id) {
    User user =
        userRepository.findById(id).orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE));
    return mapToUserResponse(user);
  }

  public UserResponse createUser(UserRequest request, MultipartFile file) {
    User user =
        User.builder()
            .firstName(request.getFirstName())
            .lastName(request.getLastName())
            .gender(request.getGender())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .role(UserRole.EMPLOYEE)
            .phone(request.getPhone())
            .joinDate(request.getJoinDate())
            .build();

    if (request.getRoleIds() != null && !request.getRoleIds().isEmpty()) {
      var roles = roleRepository.findAllById(request.getRoleIds());
      user.setRoles(new HashSet<>(roles));
    }

    userRepository.save(user);

    Asset asset = uploadFileIfPresent(file);
    if (asset != null) {
      user.setAsset(asset);
      userRepository.save(user);
    }

    return mapToUserResponse(user);
  }

  public UserResponse updateUser(Long id, UserRequest request, MultipartFile file) {
    User user =
        userRepository.findById(id).orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE));

    user.setFirstName(request.getFirstName());
    user.setLastName(request.getLastName());
    user.setGender(request.getGender());
    user.setEmail(request.getEmail());
    user.setPhone(request.getPhone());
    user.setJoinDate(request.getJoinDate());

    if (request.getRoleIds() != null) {
      var roles = roleRepository.findAllById(request.getRoleIds());
      user.setRoles(new HashSet<>(roles));
    }

    if (file != null && !file.isEmpty()) {
      if (user.getAsset() != null) {
        try {
          assetService.delete(user.getAsset().getUuid());
        } catch (IOException e) {
          throw new InternalServerError("Failed to delete old image");
        }
      }
      Asset newAsset = uploadFileIfPresent(file);
      if (newAsset != null) {
        user.setAsset(newAsset);
      }
    }
      userRepository.save(user);
    return mapToUserResponse(user);
  }

  public void deleteUser(Long id) {
    if (!userRepository.existsById(id)) {
      throw new NotFoundException(NOT_FOUND_MESSAGE);
    }
    userRepository.deleteById(id);
  }

  private UserResponse mapToUserResponse(User user) {
    UserResponse response = modelMapper.map(user, UserResponse.class);
    if (user.getAsset() != null) {
      response.setImageUrl("/api/asset/image/" + user.getAsset().getUuid());
    }
    return response;
  }

  private Asset uploadFileIfPresent(MultipartFile file) {
    if (file == null || file.isEmpty()) {
      return null;
    }
    try {
      var assetData = assetService.save(file, AssetType.USER);
      return assetRepository.findById(assetData.getId()).orElse(null);
    } catch (IOException e) {
      throw new InternalServerError("Failed to upload image");
    }
  }
}
