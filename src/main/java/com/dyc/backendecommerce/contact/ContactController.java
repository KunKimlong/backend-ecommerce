package com.dyc.backendecommerce.contact;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Public (under /api/store/**): visitors can write in without an account
@RestController
@RequestMapping("/api/store/contact")
@RequiredArgsConstructor
public class ContactController {
  private final ContactService contactService;

  @PostMapping
  public ResponseEntity<Void> send(@Valid @RequestBody ContactRequest request) {
    contactService.save(request);
    return new ResponseEntity<>(HttpStatus.CREATED);
  }
}
