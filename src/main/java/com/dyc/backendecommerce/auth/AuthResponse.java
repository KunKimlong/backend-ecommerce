package com.dyc.backendecommerce.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
class AuthResponse {
    private String accessToken;
    private int expiresIn;
    private String refreshToken;
    private int refreshExpiresIn;
}
