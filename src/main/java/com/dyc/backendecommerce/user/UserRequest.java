package com.dyc.backendecommerce.user;

import java.util.UUID;

import com.dyc.backendecommerce.shared.enums.Gender;
import com.dyc.backendecommerce.shared.validation.ValidPhone;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
  private String firstName;
  private String lastName;
  private Gender gender;
  private String email;
  private String password;
  @ValidPhone private String phone;
  private LocalDate joinDate;
  private Boolean active;
  private List<UUID> roleIds;
}
