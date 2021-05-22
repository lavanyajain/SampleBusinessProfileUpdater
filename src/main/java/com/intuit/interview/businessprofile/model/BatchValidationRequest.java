package com.intuit.interview.businessprofile.model;

import java.util.List;

public class BatchValidationRequest {
    private BusinessProfile businessProfile;
    private List<String> products;

    public BusinessProfile getBusinessProfile() {
        return businessProfile;
    }

    public void setBusinessProfile(BusinessProfile businessProfile) {
        this.businessProfile = businessProfile;
    }

    public List<String> getProducts() {
        return products;
    }

    public void setProducts(List<String> products) {
        this.products = products;
    }
}
