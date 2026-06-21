package com.dyc.backendecommerce.shared.config;

import static com.dyc.backendecommerce.shared.enums.Permission.*;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

import com.dyc.backendecommerce.shared.enums.Permission;
import java.util.List;
import org.springframework.http.HttpMethod;

public final class EndpointPermissions {

  private EndpointPermissions() {}

  public record Rule(HttpMethod method, String path, Permission permission) {
    public String authority() {
      return permission.authority();
    }
  }

  public static final List<Rule> RULES =
      List.of(
          // Product
          new Rule(POST, "/api/admin/product", PRODUCT_CREATE),
          new Rule(GET, "/api/admin/product/**", PRODUCT_READ),
          new Rule(PUT, "/api/admin/product/**", PRODUCT_UPDATE),
          new Rule(DELETE, "/api/admin/product/**", PRODUCT_DELETE),
          // Product Variant
          new Rule(PUT, "/api/admin/product-variant/**", PRODUCT_UPDATE),
          // Category
          new Rule(POST, "/api/category", CATEGORY_CREATE),
          new Rule(GET, "/api/category/**", CATEGORY_READ),
          new Rule(PUT, "/api/category/**", CATEGORY_UPDATE),
          new Rule(DELETE, "/api/category/**", CATEGORY_DELETE),
          // Banner
          new Rule(POST, "/api/admin/banner", BANNER_CREATE),
          new Rule(GET, "/api/admin/banner/**", BANNER_READ),
          new Rule(PUT, "/api/admin/banner/**", BANNER_UPDATE),
          new Rule(DELETE, "/api/admin/banner/**", BANNER_DELETE),
          // Banner Type
          new Rule(GET, "/api/banner-type/**", BANNER_TYPE_READ),
          // Option
          new Rule(POST, "/api/option", OPTION_CREATE),
          new Rule(GET, "/api/option/**", OPTION_READ),
          new Rule(PUT, "/api/option/**", OPTION_UPDATE),
          new Rule(DELETE, "/api/option/**", OPTION_DELETE),
          // Option Value
          new Rule(POST, "/api/option-value", OPTION_VALUE_CREATE),
          new Rule(GET, "/api/option-value/**", OPTION_VALUE_READ),
          new Rule(PUT, "/api/option-value/**", OPTION_VALUE_UPDATE),
          new Rule(DELETE, "/api/option-value/**", OPTION_VALUE_DELETE),
          // Asset
          new Rule(POST, "/api/asset/upload", ASSET_CREATE),
          new Rule(GET, "/api/asset", ASSET_READ),
          new Rule(DELETE, "/api/asset/**", ASSET_DELETE),
          // User
          new Rule(GET, "/api/users/**", USER_READ),
          new Rule(POST, "/api/users/**", EMPLOYEE_CREATE),
          new Rule(PUT, "/api/users/**", EMPLOYEE_UPDATE),
          new Rule(DELETE, "/api/users/**", EMPLOYEE_DELETE),
          // Roles
          new Rule(POST, "/api/admin/roles", ROLE_CREATE),
          new Rule(GET, "/api/admin/roles/**", ROLE_READ),
          new Rule(PUT, "/api/admin/roles/**", ROLE_UPDATE),
          new Rule(DELETE, "/api/admin/roles/**", ROLE_DELETE),
          // Permissions (read-only)
          new Rule(GET, "/api/admin/permissions/**", ROLE_READ),
          // Role Assignment
          new Rule(PUT, "/api/admin/users/*/roles", ROLE_UPDATE));
}
