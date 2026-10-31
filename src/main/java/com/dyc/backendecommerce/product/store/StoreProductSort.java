package com.dyc.backendecommerce.product.store;

import org.springframework.data.domain.Sort;

public enum StoreProductSort {
  NEWEST(Sort.by(Sort.Direction.DESC, "createdAt")),
  PRICE_ASC(Sort.by(Sort.Direction.ASC, "salePrice")),
  PRICE_DESC(Sort.by(Sort.Direction.DESC, "salePrice")),
  NAME_ASC(Sort.by(Sort.Direction.ASC, "name"));

  private final Sort sort;

  StoreProductSort(Sort sort) {
    // id as tie-breaker keeps pages stable when many rows share the sort value
    this.sort = sort.and(Sort.by(Sort.Direction.DESC, "id"));
  }

  public Sort toSort() {
    return sort;
  }
}
