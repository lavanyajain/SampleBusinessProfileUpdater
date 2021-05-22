package com.intuit.interview.businessprofile.model;

public class SubscriptionStatus {
    private String status;
    private String message;
    private BatchValidationResponse batchValidationResponse;
    private String texID;

    public String getTexID() {
        return texID;
    }

    public void setTexID(String texID) {
        this.texID = texID;
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

    public BatchValidationResponse getBatchValidationResponse() {
        return batchValidationResponse;
    }

    public void setBatchValidationResponse(BatchValidationResponse batchValidationResponse) {
        this.batchValidationResponse = batchValidationResponse;
    }
}
