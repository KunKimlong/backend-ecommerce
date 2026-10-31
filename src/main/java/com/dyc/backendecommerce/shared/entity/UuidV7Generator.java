package com.dyc.backendecommerce.shared.entity;

import java.security.SecureRandom;
import java.util.EnumSet;
import java.util.UUID;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.generator.BeforeExecutionGenerator;
import org.hibernate.generator.EventType;
import org.hibernate.generator.EventTypeSets;

/** RFC 9562 UUIDv7: 48-bit Unix millisecond timestamp, version 7, variant 10, then random bits. */
public class UuidV7Generator implements BeforeExecutionGenerator {
  private static final SecureRandom RANDOM = new SecureRandom();

  static UUID next() {
    long timestamp = System.currentTimeMillis();
    long randA = RANDOM.nextInt(1 << 12);
    long randB = RANDOM.nextLong() & 0x3FFFFFFFFFFFFFFFL;
    long msb = (timestamp << 16) | (0x7L << 12) | randA;
    long lsb = 0x8000000000000000L | randB;
    return new UUID(msb, lsb);
  }

  @Override
  public Object generate(
      SharedSessionContractImplementor session,
      Object owner,
      Object currentValue,
      EventType eventType) {
    return next();
  }

  @Override
  public EnumSet<EventType> getEventTypes() {
    return EventTypeSets.INSERT_ONLY;
  }
}
