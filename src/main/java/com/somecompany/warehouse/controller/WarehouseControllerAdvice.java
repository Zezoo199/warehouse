package com.somecompany.warehouse.controller;

import java.io.IOException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

/**
 *
 */
@ControllerAdvice
public class WarehouseControllerAdvice {

  /**
   * @param ex exception
   * @return ResponseEntity of string descriptive error message.
   */
  @ExceptionHandler({IOException.class})
  public ResponseEntity<String> handleBadFileFormatting(
      Exception ex) {
    return new ResponseEntity<>(
        "Bad file format, Check your file again. Detailed error  " + ex.getMessage(),
        new HttpHeaders(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler({ConstraintViolationException.class})
  public ResponseEntity<String> handleConstraintViolation(
      ConstraintViolationException ex, WebRequest request) {
    return new ResponseEntity<>(
        "ConstraintViolationException Item Already Exist error " + ex.getMessage(),
        new HttpHeaders(), HttpStatus.BAD_REQUEST);
  }
}


