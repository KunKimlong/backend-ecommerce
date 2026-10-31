package com.dyc.backendecommerce.shared.entity;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import org.hibernate.annotations.IdGeneratorType;

/**
 * Generates a time-ordered UUIDv7 primary key, so ordering by id still means oldest to newest.
 * Hibernate 6.6 has no built-in v7 style (added in Hibernate 7).
 */
@IdGeneratorType(UuidV7Generator.class)
@Retention(RUNTIME)
@Target(FIELD)
public @interface UuidV7 {}
