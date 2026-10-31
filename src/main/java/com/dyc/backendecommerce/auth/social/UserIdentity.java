package com.dyc.backendecommerce.auth.social;

import com.dyc.backendecommerce.shared.entity.Auditable;
import com.dyc.backendecommerce.shared.entity.UuidV7;
import com.dyc.backendecommerce.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** An external account (provider + its account id) that signs in as {@link #user}. */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "user_identities")
public class UserIdentity extends Auditable {
  @Id
  @UuidV7
  private UUID id;

  // Lazy: sign-in loads the user through the repository's entity graph instead
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id")
  private User user;

  private String provider;

  private String subject;
}
