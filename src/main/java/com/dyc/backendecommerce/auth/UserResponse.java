package com.dyc.backendecommerce.auth;

import com.dyc.backendecommerce.shared.enums.Gender;
import com.dyc.backendecommerce.shared.enums.UserRole;
import java.time.LocalDate;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
  private Long id;
  private String firstName;
  private String lastName;
  private String email;
  private UserRole role;
  private Gender gender;
  private String phone;
  private LocalDate joinDate;
  private String imageUrl;
  private List<String> permissions;
}
