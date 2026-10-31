package com.dyc.backendecommerce.auth.otp;

/** What a one-time code proves. A code issued for one purpose can never be used for another. */
public enum OtpPurpose {
  EMAIL_VERIFICATION,
  PASSWORD_RESET
}
