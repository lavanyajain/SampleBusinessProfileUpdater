package com.intuit.interview.businessprofile.model;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ValidationServicesResponse {
    private String status;
    private String message;

    public ValidationServicesResponse() {
        this.status = "SUCCESS";
        this.message = "Error validating data against this profile check for configuration validation for this profile and product";
    }

    public ValidationServicesResponse(String status, String message) {
        this.status = status;
        this.message = message;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
