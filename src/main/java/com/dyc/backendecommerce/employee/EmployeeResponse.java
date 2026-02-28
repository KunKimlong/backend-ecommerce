package com.dyc.backendecommerce.employee;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class EmployeeResponse {
    List<EmployeeData> employeeData;
    private long total;
    private int page;
    private int pageSize;
}
