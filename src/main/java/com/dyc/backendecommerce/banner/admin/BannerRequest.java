package com.dyc.backendecommerce.banner.admin;

import java.util.UUID;

import com.dyc.backendecommerce.banner.BannerPlacement;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    private UUID bannerTypeId;
    @Size(max = 255, message = "Description must be at most 255 characters")
    private String description;
    private String buttonName;
    private UUID productId;
    private UUID assetId;
    private Integer order;
    private LocalDate startAt;
    private LocalDate endAt;

    @NotNull(message = "Placement is required")
    private BannerPlacement placement;
}
