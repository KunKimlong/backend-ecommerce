package com.dyc.backendecommerce.option;

import java.util.UUID;
import com.dyc.backendecommerce.shared.entity.UuidV7;

import com.dyc.backendecommerce.shared.entity.Auditable;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "options")
public class Option extends Auditable {
  @Id
  @UuidV7
  private UUID id;

  private String name;
}
