package com.dyc.backendecommerce.employee;

import com.dyc.backendecommerce.asset.Asset;
import com.dyc.backendecommerce.asset.AssetRepository;
import com.dyc.backendecommerce.auth.AuthService;
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
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
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
    private final AssetRepository assetRepository;

    private Page<Employee> getAllEmployee(Specification<Employee> spec, Pageable pageable) {
        return employeeRepository.findAll(spec, pageable);
    }

    @Transactional(readOnly = true)
    public EmployeeResponse getAllEmployees(String name, LocalDate joinDate, Pageable pageable) {
        //filter employee by name and join date
        Specification<Employee> spec = Specification.allOf(
                EmployeeSpecification.hasName(name),
                EmployeeSpecification.hasJoinDate(joinDate)
        );

        Page<Employee> employees = getAllEmployee(spec, pageable);
        List<EmployeeData> employeeData = employees
                .getContent()
                .stream()
                .map(this::mapToEmployeeData)
                .toList();

        return EmployeeResponse.builder()
                .employeeData(employeeData)
                .total(employees.getTotalElements())
                .page(employees.getNumber())
                .pageSize(employees.getSize())
                .build();
    }

    public EmployeeData getEmployeeById(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(NOT_FOUND_MESSAGE));
        return mapToEmployeeData(employee);
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
                .asset(findAssetOrNull(request.getAssetId()))
                .user(user)
                .build();

        return mapToEmployeeData(employeeRepository.save(employee));
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
            employee.setAsset(findAssetOrNull(request.getAssetId()));
            employee.setUser(user);
            return mapToEmployeeData(employeeRepository.save(employee));
        } else {
            throw new NotFoundException(NOT_FOUND_MESSAGE);
        }
    }

    public void deleteEmployee(Long id) {
        Employee employee = employeeRepository.findById(id).orElse(null);
        if (employee != null) {
            employeeRepository.delete(employee);

        } else {
            throw new NotFoundException(NOT_FOUND_MESSAGE);
        }
    }

    private EmployeeData mapToEmployeeData(Employee employee) {
        //model mapper to map employee to employee data
        EmployeeData dto = modelMapper.map(employee, EmployeeData.class);
        if (employee.getUser() != null) {
            dto.setFirstName(employee.getUser().getFirstName());
            dto.setLastName(employee.getUser().getLastName());
            dto.setEmail(employee.getUser().getEmail());
            dto.setGender(employee.getUser().getGender());
        }
        return dto;
    }

    private Asset findAssetOrNull(Long assetId) {
        if (assetId == null) {
            return null;
        }
        Asset asset = assetRepository.findById(assetId).orElse(null);
        if (asset == null) {
            throw new NotFoundException("Asset not found");
        }
        return asset;
    }

}
