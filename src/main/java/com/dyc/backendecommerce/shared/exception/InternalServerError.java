package com.dyc.backendecommerce.shared.exception;

public class InternalServerError extends RuntimeException {
  public InternalServerError(String message) {
    super(message);
  }
}
