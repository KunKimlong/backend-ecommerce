package com.dyc.backendecommerce.color;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class ColorResponse {
  List<ColorData> colorData;
  private long total;
  private int page;
  private int pageSize;
}
