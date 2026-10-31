package com.dyc.backendecommerce.option;

import java.util.UUID;

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
public class OptionResponse {
  private UUID id;
  private String name;
  private AuditableResult createdBy;
  private AuditableResult updatedBy;
}
