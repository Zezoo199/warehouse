package com.somecompany.warehouse.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception to be sent ot frontend in case of bad MultiPartFile
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class FileUploadException extends Exception {

  public FileUploadException() {
  }

  public FileUploadException(String message) {
    super(message);
  }
}
