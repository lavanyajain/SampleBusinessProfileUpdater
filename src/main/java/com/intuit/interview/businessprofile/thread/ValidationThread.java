package com.intuit.interview.businessprofile.thread;

import com.intuit.interview.businessprofile.exception.ValidationConfigurationNotFoundException;
import com.intuit.interview.businessprofile.model.BusinessProfile;
import com.intuit.interview.businessprofile.model.ValidationServicesResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeoutException;

public class ValidationThread implements Callable<ValidationServicesResponse> {

    private final BusinessProfile businessProfile;

    private final String product;

    private final String HOST_UTL = "http://localhost:8080/";

    public ValidationThread(BusinessProfile businessProfile, String product) {
        this.businessProfile = businessProfile;
        this.product = product;
    }

    @Override
    public ValidationServicesResponse call() throws TimeoutException {
        ResponseEntity<ValidationServicesResponse> result = null;
        try {
            URI url = new URI(HOST_UTL + "validate/" + this.product);
            RestTemplate restTemplate = new RestTemplate();
            result = restTemplate.postForEntity(url, businessProfile, ValidationServicesResponse.class);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND)
                throw new ValidationConfigurationNotFoundException("List of products contains " + this.product + " which is invalid");
            else throw new TimeoutException(e.getMessage());
        } catch (Exception e) {
            throw new TimeoutException(e.getMessage());
        }

        return result.getBody();
    }
}
