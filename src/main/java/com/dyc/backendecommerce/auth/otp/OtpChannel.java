package com.dyc.backendecommerce.auth.otp;

import com.dyc.backendecommerce.user.User;
import java.time.Duration;

/**
 * Delivers a one-time code to a user. Email is the first channel; to add SMS or Telegram, add a Spring
 * bean implementing this and ask {@link OtpService} for that channel name. Generating, storing,
 * limiting and checking codes stays in {@link OtpService} and is the same for every channel.
 */
public interface OtpChannel {

  /** Stored with each code, e.g. {@code "EMAIL"}. */
  String channel();

  /** False when the channel isn't configured (e.g. no SMTP server). */
  boolean isEnabled();

  /** Sends {@code code}; throws if delivery fails so the caller can roll back. */
  void send(User recipient, String code, OtpPurpose purpose, Duration validFor);
}
