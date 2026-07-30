package com.dyc.backendecommerce.optionvalue;

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
public class OptionValueRequest {
    private String name;
    private Long optionId;
}
