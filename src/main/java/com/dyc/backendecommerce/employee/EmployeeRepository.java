package com.dyc.backendecommerce.employee;

import lombok.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.domain.Specification;

@Repository
public interface EmployeeRepository
    extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {
  @EntityGraph(attributePaths = {"user"})
  @NonNull
  Page<Employee> findAll(Specification<Employee> spec, @NonNull Pageable pageable);
}
