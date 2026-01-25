package com.dyc.backendecommerce.category;

import com.dyc.backendecommerce.shared.entity.AuditableResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class CategoryResponse {
    private Long id;
    private String name;
    private AuditableResult createdBy;
    private AuditableResult updatedBy;
}
