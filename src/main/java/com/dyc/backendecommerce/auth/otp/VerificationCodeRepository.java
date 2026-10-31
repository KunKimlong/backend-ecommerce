package com.dyc.backendecommerce.auth.otp;

import com.dyc.backendecommerce.user.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import jakarta.persistence.LockModeType;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, UUID> {

  Optional<VerificationCode> findFirstByUserAndPurposeOrderByCreatedAtDesc(User user, OtpPurpose purpose);

  // Row lock so parallel guesses can't each read the same attempt count and exceed the limit
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  Optional<VerificationCode> findTopByUserAndPurposeOrderByCreatedAtDesc(User user, OtpPurpose purpose);

  List<VerificationCode> findByUserAndPurposeAndConsumedAtIsNull(User user, OtpPurpose purpose);

  long countByUserAndPurposeAndCreatedAtAfter(User user, OtpPurpose purpose, LocalDateTime after);
}
