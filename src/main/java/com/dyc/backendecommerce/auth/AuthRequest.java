package com.dyc.backendecommerce.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AuthRequest {
    @Schema(defaultValue = "admin@gmail.com")
    private String email;
    @Schema(defaultValue = "admin@123")
    private String password;
    // false = session cookies only (signed out when the browser closes); omitted = remember
    @Schema(defaultValue = "true")
    private Boolean rememberMe;
}
