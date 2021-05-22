package com.intuit.interview.businessprofile.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.HashMap;

import static com.intuit.interview.businessprofile.config.Configuration.SUCCESS_STATUS;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class BatchValidationResponse {
    private String status;
    private HashMap<String, ValidationServicesResponse> validationResponse;

    public BatchValidationResponse() {
        this.status = SUCCESS_STATUS;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public HashMap<String, ValidationServicesResponse> getValidationResponse() {
        return validationResponse;
    }

    public void setValidationResponse(HashMap<String, ValidationServicesResponse> validationResponse) {
        this.validationResponse = validationResponse;
    }
}
