package com.dyc.backendecommerce.shared.util;

import java.util.List;
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
public class ResponseData<T> {
  private List<T> data;
  private long total;
  private int page;
  private int pageSize;
}
