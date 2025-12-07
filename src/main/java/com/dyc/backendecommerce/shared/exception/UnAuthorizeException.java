package com.dyc.backendecommerce.shared.exception;

public class UnAuthorizeException extends RuntimeException {
  public UnAuthorizeException(String message) {
    super(message);
  }
}
