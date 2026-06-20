package com.dyc.backendecommerce.shared.enums;

public enum Permission {
  PRODUCT_CREATE("product:create"),
  PRODUCT_READ("product:read"),
  PRODUCT_UPDATE("product:update"),
  PRODUCT_DELETE("product:delete"),
  CATEGORY_CREATE("category:create"),
  CATEGORY_READ("category:read"),
  CATEGORY_UPDATE("category:update"),
  CATEGORY_DELETE("category:delete"),
  BANNER_CREATE("banner:create"),
  BANNER_READ("banner:read"),
  BANNER_UPDATE("banner:update"),
  BANNER_DELETE("banner:delete"),
  BANNER_TYPE_READ("banner_type:read"),
  OPTION_CREATE("option:create"),
  OPTION_READ("option:read"),
  OPTION_UPDATE("option:update"),
  OPTION_DELETE("option:delete"),
  OPTION_VALUE_CREATE("option_value:create"),
  OPTION_VALUE_READ("option_value:read"),
  OPTION_VALUE_UPDATE("option_value:update"),
  OPTION_VALUE_DELETE("option_value:delete"),
  ASSET_CREATE("asset:create"),
  ASSET_READ("asset:read"),
  ASSET_DELETE("asset:delete"),
  USER_READ("user:read"),
  EMPLOYEE_CREATE("employee:create"),
  EMPLOYEE_UPDATE("employee:update"),
  EMPLOYEE_DELETE("employee:delete"),
  ROLE_CREATE("role:create"),
  ROLE_READ("role:read"),
  ROLE_UPDATE("role:update"),
  ROLE_DELETE("role:delete");

  private final String authority;

  Permission(String authority) {
    this.authority = authority;
  }

  public String authority() {
    return authority;
  }
}
