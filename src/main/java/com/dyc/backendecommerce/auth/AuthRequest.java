package com.dyc.backendecommerce.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
class AuthRequest {
    @Schema(defaultValue = "admin@gmail.com")
    private String username;
    @Schema(defaultValue = "admin@123")
    private String password;
}
