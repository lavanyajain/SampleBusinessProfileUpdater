package com.intuit.interview.businessprofile.exception;

public class ValidationConfigurationNotFoundException extends RuntimeException {
    ValidationConfigurationNotFoundException(String message) {
        super(message);
    }
}
