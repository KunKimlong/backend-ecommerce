package com.dyc.backendecommerce.shared.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A real phone number in international form, e.g. {@code +85512345678}, checked against the
 * numbering plan of its country (the same libphonenumber rules both frontends use). Null and blank
 * pass, so pair it with {@code @NotBlank} where the number is required.
 */
@Documented
@Constraint(validatedBy = PhoneValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPhone {
  String message() default "Enter a valid phone number with its country code, e.g. +85512345678";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
