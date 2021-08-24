package com.somecompany.warehouse.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception when not enough articles in inventory to compose the product.
 */
@ResponseStatus(HttpStatus.EXPECTATION_FAILED)
public class ProductOutOfStockException extends Exception {

  public ProductOutOfStockException() {
    super();
  }

  public ProductOutOfStockException(String message) {
    super(message);
  }
}
