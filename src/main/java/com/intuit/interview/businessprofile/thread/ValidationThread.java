package com.intuit.interview.businessprofile.thread;

import com.intuit.interview.businessprofile.model.BusinessProfile;
import com.intuit.interview.businessprofile.model.ValidationServicesResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;

public class ValidationThread implements Callable<ValidationServicesResponse> {

    private final BusinessProfile businessProfile;

    private final String product;

    public ValidationThread(BusinessProfile businessProfile, String product) {
        this.businessProfile = businessProfile;
        this.product = product;
    }

    @Override
    public ValidationServicesResponse call() throws TimeoutException {
        ResponseEntity<ValidationServicesResponse> result = null;
        try {
            URI url = new URI("http://localhost:8080/validate/" + this.product);
            RestTemplate restTemplate = new RestTemplate();
            result = restTemplate.postForEntity(url, businessProfile, ValidationServicesResponse.class);
        } catch (Exception e) {
            throw new TimeoutException(e.getMessage());
        }

        return result.getBody();
    }
}
