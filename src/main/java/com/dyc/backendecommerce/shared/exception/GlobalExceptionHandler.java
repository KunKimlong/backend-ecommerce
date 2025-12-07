package com.dyc.backendecommerce.shared.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<Map<String, Object>> handleEmployeeNotFound(NotFoundException ex) {
    Map<String, Object> body = new HashMap<>();
    body.put("error", ex.getMessage());
    body.put("status", HttpStatus.NOT_FOUND.value());
    body.put("timestamp", LocalDateTime.now());
    return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(UnAuthorizeException.class)
  public ResponseEntity<ExceptionDto> handleUnAuthorizeException(UnAuthorizeException ex) {
    ExceptionDto body = ExceptionDto
            .builder()
            .timestamp(LocalDateTime.now())
            .message(ex.getMessage())
            .status(HttpStatus.UNAUTHORIZED)
            .build();
    return new ResponseEntity<>(body, HttpStatus.UNAUTHORIZED);
  }
}
