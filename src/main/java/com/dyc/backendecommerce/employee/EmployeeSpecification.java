package com.dyc.backendecommerce.employee;

import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDate;

public class EmployeeSpecification {

  public static Specification<Employee> hasName(String name) {
    return (root, query, cb) -> {
      if (name == null || name.isEmpty()) {
        return null;
      }
      String pattern = "%" + name.toLowerCase() + "%";
      return cb.or(
          cb.like(cb.lower(root.get("user").get("firstName")), pattern),
          cb.like(cb.lower(root.get("user").get("lastName")), pattern));
    };
  }

  public static Specification<Employee> hasJoinDate(LocalDate joinDate) {
    return (root, query, cb) -> {
      if (joinDate == null) {
        return null;
      }
      return cb.equal(root.get("joinDate"), joinDate);
    };
  }
}
