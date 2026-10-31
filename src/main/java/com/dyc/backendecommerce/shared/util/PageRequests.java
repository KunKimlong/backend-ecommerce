package com.dyc.backendecommerce.shared.util;

import com.dyc.backendecommerce.shared.exception.BadRequestException;
import java.util.Set;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public final class PageRequests {
  private PageRequests() {}

  /**
   * A page sorted by a column the caller may sort on. sortBy comes from the query string, so it is checked
   * against the endpoint's list: an unknown name would otherwise fail as a 500, and any entity path (such as
   * a password hash) could be used to order rows.
   */
  public static Pageable sorted(
      int page, int size, String sortBy, boolean ascending, Set<String> sortableProperties) {
    if (!sortableProperties.contains(sortBy)) {
      throw new BadRequestException("Cannot sort by '" + sortBy + "'");
    }
    Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
    return PageRequest.of(page, size, sort);
  }
}
