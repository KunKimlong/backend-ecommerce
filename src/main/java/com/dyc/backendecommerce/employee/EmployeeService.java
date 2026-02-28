package com.dyc.backendecommerce.employee;

import com.dyc.backendecommerce.auth.AuthService;
import com.dyc.backendecommerce.color.Color;
import com.dyc.backendecommerce.color.ColorData;
import com.dyc.backendecommerce.color.ColorResponse;
import com.dyc.backendecommerce.shared.enums.UserRole;
import com.dyc.backendecommerce.shared.exception.NotFoundException;
import com.dyc.backendecommerce.user.User;
import com.dyc.backendecommerce.user.UserRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional
@Service
@AllArgsConstructor
public class EmployeeService {
    private static final String NOT_FOUND_MESSAGE = "Employees not found in list";

    private final EmployeeRepository employeeRepository;
    private final AuthService authService;
    private final ModelMapper modelMapper;

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    private Page<Employee> getAllEmployee(Pageable pageable) {
        return employeeRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public EmployeeResponse getAllEmployees(Pageable pageable) {
        Page<Employee> employees = getAllEmployee(pageable);
        List<EmployeeData> employeeData= employees
                .getContent()
                .stream()
                .map(employee -> modelMapper.map(employee, EmployeeData.class))
                .toList();

        return EmployeeResponse.builder()
                .employeeData(employeeData)
                .total(employees.getTotalElements())
                .page(employees.getNumber())
                .pageSize(employees.getSize())
                .build();
    }

    public EmployeeData saveEmployee(EmployeeRequest request) {
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .gender(request.getGender())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(UserRole.EMPLOYEE)
                .build();
        userRepository.save(user);
        Employee employee = Employee.builder()
                .phone(request.getPhone())
                .joinDate(request.getJoinDate())
                .user(user)
                .build();

        return modelMapper.map(employeeRepository.save(employee), EmployeeData.class);
    }

    public EmployeeData updateEmployee(Long id, EmployeeRequest request) {
        Employee employee = employeeRepository.findById(id).orElse(null);
        if (employee != null) {
            User user = employee.getUser();
            if (user == null) {
                user = User.builder().role(UserRole.EMPLOYEE).build();
            }
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setGender(request.getGender());
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setRole(UserRole.EMPLOYEE);
            userRepository.save(user);

            employee.setPhone(request.getPhone());
            employee.setJoinDate(request.getJoinDate());
            employee.setUser(user);
            return this.modelMapper.map(employeeRepository.save(employee)
                    , EmployeeData.class);
        } else {
            throw new NotFoundException(NOT_FOUND_MESSAGE);
        }
    }

    public void deleteCategory(Long id) {
        Employee employee = employeeRepository.findById(id).orElse(null);
        if (employee != null) {
            employeeRepository.delete(employee);

        } else {
            throw new NotFoundException(NOT_FOUND_MESSAGE);
        }
    }

}
