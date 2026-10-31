package com.dyc.backendecommerce.product.store;

import com.dyc.backendecommerce.product.Product;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.data.jpa.domain.Specification;

/** Storefront catalog filters; every value is bound as a query parameter, never concatenated. */
public record StoreProductFilter(String q, UUID categoryId, BigDecimal minPrice, BigDecimal maxPrice) {
  // Caps the number of LIKE clauses a single query can generate
  private static final int MAX_TERMS = 5;
  // The trigram index needs this many characters to narrow a "contains" search
  private static final int MIN_CONTAINS_LENGTH = 3;

  /**
   * One escaped LIKE pattern per search word, for both product and category names. Type-ahead on a
   * single word shorter than {@link #MIN_CONTAINS_LENGTH} matches name starts ("i" → "iPhone…"),
   * which is what the shopper means and stays on the index; everything else matches anywhere.
   */
  public List<String> namePatterns(boolean typeAhead) {
    List<String> terms = terms();
    boolean prefix = typeAhead && terms.size() == 1 && terms.get(0).length() < MIN_CONTAINS_LENGTH;
    return terms.stream().map(term -> (prefix ? "" : "%") + escapeLike(term) + "%").toList();
  }

  /**
   * Each word has to appear somewhere, in any order: "samsung s10" finds "Samsung Galaxy S10", and a
   * category word like "laptop" finds products filed under it. Matching categories are resolved by
   * the caller (pattern → category ids) so this query never joins categories: name LIKE and
   * category_id IN then each use their own index instead of forcing a scan of every product.
   */
  public Specification<Product> toSpecification(Map<String, List<UUID>> categoryIdsByPattern) {
    return (root, query, cb) -> {
      List<Predicate> predicates = new ArrayList<>();
      Expression<String> name = cb.lower(root.get("name"));
      categoryIdsByPattern.forEach(
          (pattern, categoryIds) -> {
            Predicate byName = cb.like(name, pattern, '\\');
            predicates.add(
                categoryIds.isEmpty()
                    ? byName
                    : cb.or(byName, root.get("category").get("id").in(categoryIds)));
          });
      if (categoryId != null) {
        predicates.add(cb.equal(root.get("category").get("id"), categoryId));
      }
      if (minPrice != null) {
        predicates.add(cb.greaterThanOrEqualTo(root.get("salePrice"), minPrice));
      }
      if (maxPrice != null) {
        predicates.add(cb.lessThanOrEqualTo(root.get("salePrice"), maxPrice));
      }
      return cb.and(predicates.toArray(Predicate[]::new));
    };
  }

  /**
   * Same matching as {@link #toSpecification(Map)}, ranked for type-ahead: names starting with the
   * first word, then names containing it, then category-only matches, alphabetically within each.
   * Spring Data keeps this ordering for unsorted pages and drops it from the count query.
   */
  public Specification<Product> toSuggestionSpecification(Map<String, List<UUID>> categoryIdsByPattern) {
    return (root, query, cb) -> {
      Predicate matches = toSpecification(categoryIdsByPattern).toPredicate(root, query, cb);
      List<String> terms = terms();
      if (!terms.isEmpty()) {
        Expression<String> name = cb.lower(root.get("name"));
        String first = escapeLike(terms.get(0));
        Expression<Integer> rank =
            cb.<Integer>selectCase()
                .when(cb.like(name, first + "%", '\\'), 0)
                .when(cb.like(name, "%" + first + "%", '\\'), 1)
                .otherwise(2);
        query.orderBy(cb.asc(rank), cb.asc(name));
      }
      return matches;
    };
  }

  private List<String> terms() {
    if (q == null || q.isBlank()) {
      return List.of();
    }
    return Arrays.stream(q.trim().toLowerCase().split("\\s+")).distinct().limit(MAX_TERMS).toList();
  }

  private static String escapeLike(String value) {
    return value.replace("\\", "\\\\").replace("%", "\\%").replace("_", "\\_");
  }
}
