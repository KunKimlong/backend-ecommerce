package com.dyc.backendecommerce.contact;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ContactService {
  private final ContactMessageRepository contactMessageRepository;

  public void save(ContactRequest request) {
    contactMessageRepository.save(
        ContactMessage.builder()
            .name(request.getName().trim())
            .email(request.getEmail().trim())
            .phone(request.getPhone() == null || request.getPhone().isBlank() ? null : request.getPhone().trim())
            .subject(request.getSubject().trim())
            .message(request.getMessage().trim())
            .build());
  }
}
