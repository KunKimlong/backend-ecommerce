package com.dyc.backendecommerce.employee;

import com.dyc.backendecommerce.shared.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmployeeRequest {
    // User fields
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;

    private Gender gender;
    @Email
    @NotBlank
    private String email;
    @NotBlank
    private String password;
    // Employee fields
    @NotBlank
    private String phone;
    @NotNull
    private LocalDate joinDate;
    private Long assetId;
    // getters and setters
}
