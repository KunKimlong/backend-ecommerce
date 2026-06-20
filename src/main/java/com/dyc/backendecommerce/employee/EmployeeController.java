package com.dyc.backendecommerce.employee;

import jakarta.validation.Valid;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/employee")
@AllArgsConstructor
public class EmployeeController {
  private final EmployeeService employeeService;

  @GetMapping
  public ResponseEntity<EmployeeResponse> getEmployee(
      @RequestParam(required = false) String name,
      @RequestParam(required = false) LocalDate joinDate,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "5") int size,
      @RequestParam(defaultValue = "id") String sortBy,
      @RequestParam(defaultValue = "false") boolean ascending) {
    Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
    Pageable pageable = PageRequest.of(page, size, sort);
    return new ResponseEntity<>(
        employeeService.getAllEmployees(name, joinDate, pageable), HttpStatus.OK);
  }

  @GetMapping("/{id}")
  public ResponseEntity<EmployeeData> getEmployeeById(@PathVariable Long id) {
    return new ResponseEntity<>(employeeService.getEmployeeById(id), HttpStatus.OK);
  }

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<EmployeeData> createEmployee(
          @RequestPart("data") @Valid EmployeeRequest request,
          @RequestPart(value = "file", required = false) MultipartFile file
  ) {
    return ResponseEntity.status(HttpStatus.CREATED)
            .body(employeeService.saveEmployee(request, file));
  }

  @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<EmployeeData> updateEmployee(
          @PathVariable long id,
          @RequestPart("data") EmployeeRequest request,
          @RequestPart(value = "file", required = false) MultipartFile file
  ) {
    return ResponseEntity.ok(employeeService.updateEmployee(id, request, file));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteEmployee(@PathVariable long id) {
    employeeService.deleteEmployee(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
