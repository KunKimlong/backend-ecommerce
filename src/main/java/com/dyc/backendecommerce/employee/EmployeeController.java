package com.dyc.backendecommerce.employee;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employee")
@AllArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;

    @GetMapping
    public ResponseEntity<EmployeeResponse> getEmployee(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "false") boolean ascending) {
        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return new ResponseEntity<>(employeeService.getAllEmployees(pageable), HttpStatus.OK);
    }


    @PostMapping
    public ResponseEntity<EmployeeData> createEmployee(@RequestBody EmployeeRequest request) {

        return new ResponseEntity<>(employeeService.saveEmployee(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeData> updateEmployee(
            @PathVariable long id, @RequestBody EmployeeRequest employeeRequest) {

        return new ResponseEntity<>(employeeService.updateEmployee(id, employeeRequest), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable long id) {

        employeeService.deleteCategory(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
