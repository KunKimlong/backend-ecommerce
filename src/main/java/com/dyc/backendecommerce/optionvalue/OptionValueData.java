package com.dyc.backendecommerce.optionvalue;

import java.util.UUID;

import com.dyc.backendecommerce.option.OptionData;
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
  private UUID id;
  private String name;
  private OptionData option;
}
