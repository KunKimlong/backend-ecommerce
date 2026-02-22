package com.dyc.backendecommerce.color;


import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
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
public class ColorRequest {
    @Schema(defaultValue = "White")
    String name;
    @NotBlank(message = "Color is required")
    @Size(min = 7, max = 7, message = "Color must be exactly 7 characters")
    @Pattern(
            regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$",
            message = "Code must start with # and contain 6 characters after it"
    )
            @Schema(defaultValue = "#FFFFFF")
    String code;
}
