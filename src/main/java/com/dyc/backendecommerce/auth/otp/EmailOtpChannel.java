package com.dyc.backendecommerce.auth.otp;

import com.dyc.backendecommerce.shared.exception.InternalServerError;
import com.dyc.backendecommerce.store.StoreService;
import com.dyc.backendecommerce.user.User;
import java.time.Duration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

/** Sends one-time codes by email through the SMTP server in {@code spring.mail.*}. */
@Slf4j
@Component
public class EmailOtpChannel implements OtpChannel {
  public static final String CHANNEL = "EMAIL";

  private final ObjectProvider<JavaMailSender> mailSender;
  private final StoreService storeService;
  private final String host;
  private final String from;

  public EmailOtpChannel(
      ObjectProvider<JavaMailSender> mailSender,
      StoreService storeService,
      @Value("${spring.mail.host:}") String host,
      @Value("${spring.application.mail.from:}") String from,
      @Value("${spring.mail.username:}") String username) {
    this.mailSender = mailSender;
    this.storeService = storeService;
    this.host = host;
    this.from = from.isBlank() ? username : from;
  }

  @Override
  public String channel() {
    return CHANNEL;
  }

  @Override
  public boolean isEnabled() {
    return !host.isBlank() && mailSender.getIfAvailable() != null;
  }

  @Override
  public void send(User recipient, String code, OtpPurpose purpose, Duration validFor) {
    String store = storeService.getStoreInfo().name();
    SimpleMailMessage message = new SimpleMailMessage();
    if (!from.isBlank()) {
      message.setFrom(from);
    }
    message.setTo(recipient.getEmail());
    String what =
        switch (purpose) {
          case EMAIL_VERIFICATION -> "verification code";
          case PASSWORD_RESET -> "password reset code";
        };
    String ignoreIf =
        switch (purpose) {
          case EMAIL_VERIFICATION -> "If you didn't create an account, you can ignore this email.";
          case PASSWORD_RESET ->
              "If you didn't ask to reset your password, you can ignore this email; your password stays the same.";
        };
    message.setSubject("%s is your %s %s".formatted(code, store, what));
    message.setText(
        """
        Hi %s,

        Your %s %s is:

            %s

        It expires in %d minutes. Never share it with anyone. %s
        """
            .formatted(recipient.getFirstName(), store, what, code, validFor.toMinutes(), ignoreIf));
    try {
      mailSender.getObject().send(message);
    } catch (MailException e) {
      log.warn("Could not send verification code to user {}", recipient.getId(), e);
      // Rolls back the code (and a new sign-up) with it; the message hides SMTP details from the client
      throw new InternalServerError("We couldn't send the verification email. Please try again later.");
    }
  }
}
