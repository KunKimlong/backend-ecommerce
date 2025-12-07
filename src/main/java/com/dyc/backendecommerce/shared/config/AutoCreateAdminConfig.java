package com.dyc.backendecommerce.shared.config;

import com.dyc.backendecommerce.shared.enums.UserRole;
import com.dyc.backendecommerce.user.User;
import com.dyc.backendecommerce.user.UserRepository;
import com.dyc.backendecommerce.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AutoCreateAdminConfig implements CommandLineRunner {

  private final UserService userService;

  @Value("${spring.application.admin-generate.email}")
  private String email;

  @Value("${spring.application.admin-generate.password}")
  private String password;

  @Value("${spring.application.admin-generate.first-name}")
  private String firstName;

  @Value("${spring.application.admin-generate.last-name}")
  private String lastName;

  @Override
  public void run(String... args) throws Exception {
    log.info("Auto Create Admin Config");

    User existUser = userService.findByEmail(email);
    if (existUser == null) {
      User request =
          User.builder()
              .firstName(firstName)
              .lastName(lastName)
              .email(email)
              .password(password)
              .role(UserRole.ADMIN)
              .build();
      userService.save(request);
      log.info("Admin created");
    }
  }
}
