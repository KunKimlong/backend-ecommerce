package com.dyc.backendecommerce.auth.password;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// Public (under /api/auth/**): the emailed code is the proof of identity
@RestController
@RequestMapping("api/auth/password")
@RequiredArgsConstructor
public class PasswordResetController {
  private final PasswordResetService passwordResetService;

  @PostMapping("/forgot")
  public ResponseEntity<Void> forgot(@Valid @RequestBody ForgotPasswordRequest request) {
    return passwordResetService.requestReset(request);
  }

  @PostMapping("/verify-code")
  public ResponseEntity<Void> verifyCode(@Valid @RequestBody VerifyResetCodeRequest request) {
    return passwordResetService.verifyCode(request);
  }

  @PostMapping("/reset")
  public ResponseEntity<Void> reset(@Valid @RequestBody ResetPasswordRequest request) {
    return passwordResetService.resetPassword(request);
  }
}
