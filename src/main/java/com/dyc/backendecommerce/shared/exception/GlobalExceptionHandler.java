package com.dyc.backendecommerce.shared.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

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
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ExceptionDto> handleValidation(MethodArgumentNotValidException ex) {
    AtomicReference<String> errorMessage = new AtomicReference<>("");

    ex.getBindingResult()
            .getFieldErrors()
            .forEach(error -> errorMessage.set(error.getDefaultMessage()));

    ExceptionDto body = ExceptionDto.builder()
            .timestamp(LocalDateTime.now())
            .message(errorMessage.get())
            .status(HttpStatus.BAD_REQUEST)
            .build();

    return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
  }
  @ExceptionHandler(DuplicateException.class)
  public ResponseEntity<ExceptionDto> handleDuplicate(DuplicateException ex) {

    ExceptionDto body = ExceptionDto.builder()
            .timestamp(LocalDateTime.now())
            .status(HttpStatus.CONFLICT)
            .message(ex.getMessage())
            .build();

    return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
  }

  @ExceptionHandler(BadRequestException.class)
  public ResponseEntity<ExceptionDto> handleBadRequest(BadRequestException ex) {
    ExceptionDto body = ExceptionDto
            .builder()
            .timestamp(LocalDateTime.now())
            .message(ex.getMessage())
            .status(HttpStatus.BAD_REQUEST)
            .build();
    return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<ExceptionDto> handleInternalServerError(InternalServerError ex) {
    ExceptionDto body = ExceptionDto
            .builder()
            .timestamp(LocalDateTime.now())
            .message(ex.getMessage())
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .build();
    return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
