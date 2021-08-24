package com.somecompany.warehouse.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception when product is not found
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ProductNotFoundException extends Exception {

  public ProductNotFoundException() {
    super();
  }

  public ProductNotFoundException(String s) {
    super(s);
  }
}
