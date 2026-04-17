package com.dyc.backendecommerce.employee;

import com.dyc.backendecommerce.shared.enums.UserRole;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
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

  public static Specification<Employee> roleEmployee() {
    return (root, query, cb) -> cb.equal(root.join("user").get("role"), UserRole.EMPLOYEE);
  }
}
