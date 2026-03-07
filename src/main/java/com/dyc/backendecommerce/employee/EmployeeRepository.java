package com.dyc.backendecommerce.employee;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.domain.Specification;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>, JpaSpecificationExecutor<Employee> {
  @Override
  @EntityGraph(attributePaths = {"user", "asset"})
  Page<Employee> findAll(Pageable pageable);

  @EntityGraph(attributePaths = {"user", "asset"})
  Page<Employee> findAll(Specification<Employee> spec, Pageable pageable);
}
