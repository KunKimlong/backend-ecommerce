package com.dyc.backendecommerce.employee;

import com.dyc.backendecommerce.shared.entity.Auditable;
import com.dyc.backendecommerce.asset.Asset;
import com.dyc.backendecommerce.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "employees")
public class Employee extends Auditable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String phone;
  private LocalDate joinDate;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "asset_id")
  private Asset asset;

  @OneToOne
  @JoinColumn(name = "user_id", nullable = false, unique = true)
  private User user;
}
