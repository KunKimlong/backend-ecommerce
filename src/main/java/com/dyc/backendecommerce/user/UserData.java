package com.dyc.backendecommerce.user;

import java.util.UUID;

import com.dyc.backendecommerce.shared.enums.Gender;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class UserData {
  private UUID id;
  private String firstName;
  private String lastName;
  private String email;
  private Gender gender;
  private boolean active;
  private String phone;
  private LocalDate joinDate;
  private String imageUrl;
}
