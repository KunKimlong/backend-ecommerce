package com.dyc.backendecommerce.banner.admin;

import java.time.LocalDate;
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
public class BannerRequest {
    private String label;
    private String headerLabel;
    private String type;
    private String description;
    private String buttonName;
    private Long productId;
    private Long assetId;
    private LocalDate startAt;
    private LocalDate endAt;
}
