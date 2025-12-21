package com.handgrow.demo.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(DemoUserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFound(DemoUserNotFoundException ex) {
        // Return a 404 Not Found response with the exception message
        return ResponseEntity.status(404).body(ex.getMessage());
    }
}
