package com.dyc.backendecommerce.employee;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
  @Override
  @EntityGraph(attributePaths = "user")
  Page<Employee> findAll(Pageable pageable);
}
