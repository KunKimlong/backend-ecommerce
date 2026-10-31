package com.dyc.backendecommerce.contact;

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
@Table(name = "contact_messages")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ContactMessage extends Auditable {
  @Id
  @UuidV7
  private UUID id;

  private String name;
  private String email;
  private String phone;
  private String subject;
  private String message;
}
