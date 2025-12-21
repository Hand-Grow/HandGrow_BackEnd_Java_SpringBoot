package com.handgrow.demo.exception;

public class DemoUserNotFoundException extends RuntimeException {
    public DemoUserNotFoundException(String message) {
        super(message);
    }
}
