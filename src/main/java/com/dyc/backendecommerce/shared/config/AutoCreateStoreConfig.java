package com.dyc.backendecommerce.shared.config;

import com.dyc.backendecommerce.store.Store;
import com.dyc.backendecommerce.store.StoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AutoCreateStoreConfig implements CommandLineRunner {
  private final StoreRepository storeRepository;

  @Value("${spring.application.store-generate.name}")
  private String name;

  @Value("${spring.application.store-generate.description}")
  private String description;

  @Value("${spring.application.store-generate.location}")
  private String location;

  @Value("${spring.application.store-generate.support-email}")
  private String supportEmail;

  @Value("${spring.application.store-generate.support-phone-number}")
  private String supportPhoneNumber;

  @Value("${spring.application.store-generate.is-active}")
  private Boolean isActive;

  @Override
  public void run(String... args) {
    if (storeRepository.count() > 0) {
      return;
    }

    Store store =
        Store.builder()
            .name(name)
            .description(description)
            .location(location)
            .supportEmail(supportEmail)
            .supportPhoneNumber(supportPhoneNumber)
            .isActive(isActive)
            .build();

    storeRepository.save(store);
    log.info("Default store created from .env");
  }
}
