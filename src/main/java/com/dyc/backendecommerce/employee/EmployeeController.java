package com.dyc.backendecommerce.employee;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/employee")
@AllArgsConstructor
public class EmployeeController {
  private final EmployeeService employeeService;
  private final ObjectMapper objectMapper;

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
      @RequestPart("data")
          @Parameter(
              schema =
                  @Schema(type = "string", format = "json", implementation = EmployeeRequest.class))
          String dataJson,
      @RequestPart(value = "file", required = false) MultipartFile file)
      throws Exception {
    EmployeeRequest request = objectMapper.readValue(dataJson, EmployeeRequest.class);
    return new ResponseEntity<>(employeeService.saveEmployee(request, file), HttpStatus.CREATED);
  }

  @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<EmployeeData> updateEmployee(
      @PathVariable long id,
      @RequestPart("data")
          @Parameter(
              schema =
                  @Schema(type = "string", format = "json", implementation = EmployeeRequest.class))
          String dataJson,
      @RequestPart(value = "file", required = false) MultipartFile file)
      throws Exception {
    EmployeeRequest request = objectMapper.readValue(dataJson, EmployeeRequest.class);
    return new ResponseEntity<>(employeeService.updateEmployee(id, request, file), HttpStatus.OK);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteEmployee(@PathVariable long id) {
    employeeService.deleteEmployee(id);
    return new ResponseEntity<>(HttpStatus.OK);
  }
}
