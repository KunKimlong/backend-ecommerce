package com.dyc.backendecommerce.user;

import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

/** Admin user-list filters; every value is bound as a query parameter, never concatenated. */
public record UserFilter(String name, LocalDate joinDate) {

  public Specification<User> toSpecification() {
    return (root, query, cb) -> {
      List<Predicate> predicates = new ArrayList<>();
      if (name != null && !name.isBlank()) {
        // Matches first name, last name, or the full name as typed ("Kim Long")
        String pattern = "%" + escapeLike(name.trim().toLowerCase()) + "%";
        predicates.add(
            cb.like(
                cb.lower(cb.concat(cb.concat(root.get("firstName"), " "), root.get("lastName"))),
                pattern,
                '\\'));
      }
      if (joinDate != null) {
        predicates.add(cb.equal(root.get("joinDate"), joinDate));
      }
      return cb.and(predicates.toArray(Predicate[]::new));
    };
  }

  private static String escapeLike(String value) {
    return value.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
  }
}
