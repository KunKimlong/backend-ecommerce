package com.dyc.backendecommerce.shared.validation;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class PhoneValidator implements ConstraintValidator<ValidPhone, String> {
  private static final PhoneNumberUtil PHONE_UTIL = PhoneNumberUtil.getInstance();
  // The frontends send E.164; this leaves room for the spaces and dashes of a hand-typed number
  private static final int MAX_LENGTH = 25;

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null || value.isBlank()) return true;

    String number = value.trim();
    // No default region: without a leading + the country would be a guess
    if (!number.startsWith("+") || number.length() > MAX_LENGTH) return false;

    try {
      return PHONE_UTIL.isValidNumber(PHONE_UTIL.parse(number, null));
    } catch (NumberParseException e) {
      return false;
    }
  }
}
