package me.bsuir.easyattend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

  @SuppressWarnings("checkstyle:Indentation")
  @ExceptionHandler(EventNotFoundException.class)
  public ResponseEntity<String> handleEventNotFoundException(EventNotFoundException ex) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
  }
}