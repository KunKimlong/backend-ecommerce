package com.dyc.backendecommerce.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class AuthResponse {
    private String accessToken;
    private int expiresIn;
    private String refreshToken;
    private int refreshExpiresIn;
}
