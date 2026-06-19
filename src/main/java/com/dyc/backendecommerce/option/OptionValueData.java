package com.dyc.backendecommerce.option;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class OptionValueData {
  private Long id;
  private String name;
  private OptionData option;
}
