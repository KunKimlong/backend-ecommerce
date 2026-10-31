package com.dyc.backendecommerce.auth.otp;

import com.dyc.backendecommerce.shared.entity.Auditable;
import com.dyc.backendecommerce.shared.entity.UuidV7;
import com.dyc.backendecommerce.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** A one-time code sent to {@link #user}. Only its hash is kept; the code itself exists only in the message. */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Table(name = "verification_codes")
public class VerificationCode extends Auditable {
  @Id
  @UuidV7
  private UUID id;

  // Lazy: codes are always looked up for a user that is already loaded
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id")
  private User user;

  @Enumerated(EnumType.STRING)
  private OtpPurpose purpose;

  private String channel;

  private String codeHash;

  private LocalDateTime expiresAt;

  private int attempts;

  private LocalDateTime consumedAt;
}
