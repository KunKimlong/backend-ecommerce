package com.dyc.backendecommerce.user;

import com.dyc.backendecommerce.shared.enums.Gender;
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
  private String phone;
  private LocalDate joinDate;
  private List<Long> roleIds;
}
