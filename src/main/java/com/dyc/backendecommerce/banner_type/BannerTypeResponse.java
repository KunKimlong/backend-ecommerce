package com.dyc.backendecommerce.banner_type;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
public class BannerTypeResponse {
    private UUID id;
    private String name;
}
