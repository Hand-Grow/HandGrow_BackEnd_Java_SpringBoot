package com.handgrow.demo.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {
    // General Errors
    INTERNAL_SERVER_ERROR(1000, "Internal server error", HttpStatus.INTERNAL_SERVER_ERROR),
    INVALID_REQUEST(1001, "Invalid request", HttpStatus.BAD_REQUEST),

    // Auth & Account Errors
    UNAUTHORIZED(2000, "User not authenticated", HttpStatus.UNAUTHORIZED),
    FORBIDDEN(2001, "You don't have permission to access this resource", HttpStatus.FORBIDDEN),
    USER_NOT_FOUND(2002, "User not found", HttpStatus.NOT_FOUND),
    INVALID_CREDENTIALS(2003, "Invalid username or password", HttpStatus.UNAUTHORIZED),
    ACCOUNT_NOT_FOUND(2004, "Account not found", HttpStatus.NOT_FOUND),

    // Resource Errors
    RESOURCE_NOT_FOUND(3000, "Resource not found", HttpStatus.NOT_FOUND),
    PRODUCT_NOT_FOUND(3001, "Product not found", HttpStatus.NOT_FOUND),
    CONTRACT_NOT_FOUND(3002, "Contract not found", HttpStatus.NOT_FOUND),
    PLOT_NOT_FOUND(3003, "Plot not found", HttpStatus.NOT_FOUND),
    DIARY_NOT_FOUND(3004, "Diary not found", HttpStatus.NOT_FOUND);

    private final int code;
    private final String message;
    private final HttpStatus httpStatus;

    ErrorCode(int code, String message, HttpStatus httpStatus) {
        this.code = code;
        this.message = message;
        this.httpStatus = httpStatus;
    }
}
