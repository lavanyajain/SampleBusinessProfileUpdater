package com.intuit.interview.businessprofile.model;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

import static com.intuit.interview.businessprofile.config.Configuration.SUCCESS_STATUS;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class SubscriptionResponse {
    private String status;
    private String message;
    private String taxID;
    private List<String> subscriptionList;

    public SubscriptionResponse() {
        this.status = SUCCESS_STATUS;
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

    public String getTaxID() {
        return taxID;
    }

    public void setTaxID(String taxID) {
        this.taxID = taxID;
    }

    public List<String> getSubscriptionList() {
        return subscriptionList;
    }

    public void setSubscriptionList(List<String> subscriptionList) {
        this.subscriptionList = subscriptionList;
    }
}
