package com.dyc.backendecommerce.color;

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
public class ColorData {
    private Long id;
    private String name;
    private String code;
    private AuditableResult createdBy;
    private AuditableResult updatedBy;
}
