package com.dyc.backendecommerce.optionvalue;

import java.util.UUID;

import com.dyc.backendecommerce.option.OptionResponse;
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
public class OptionValueResponse {
  private UUID id;
  private String name;
  private OptionResponse option;
  private AuditableResult createdBy;
  private AuditableResult updatedBy;
}
