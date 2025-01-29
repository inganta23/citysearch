package com.example.citysearch.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {
    Map<String, Object> response = new HashMap<>();
    response.put("status", HttpStatus.INTERNAL_SERVER_ERROR.value());
    response.put("error", "Internal Server Error");
    response.put("message", ex.getMessage());
    response.put("path", request.getDescription(false).replace("uri=", ""));

    ex.printStackTrace();

    return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
    Map<String, Object> response = new HashMap<>();
    response.put("status", HttpStatus.BAD_REQUEST.value());
    response.put("error", "Bad Request");
    response.put("message", ex.getMessage());
    response.put("path", request.getDescription(false).replace("uri=", ""));

    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }
}