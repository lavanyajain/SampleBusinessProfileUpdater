package com.intuit.interview.businessprofile.services;

import com.intuit.interview.businessprofile.api.ValidationApi;
import com.intuit.interview.businessprofile.exception.InvalidDataException;
import com.intuit.interview.businessprofile.exception.ValidationConfigurationException;
import com.intuit.interview.businessprofile.exception.ValidationConfigurationNotFoundException;
import com.intuit.interview.businessprofile.model.BatchValidationRequest;
import com.intuit.interview.businessprofile.model.BatchValidationResponse;
import com.intuit.interview.businessprofile.model.BusinessProfile;
import com.intuit.interview.businessprofile.model.ValidationServicesResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import static com.intuit.interview.businessprofile.config.Configuration.FAILURE_STATUS;
import static com.intuit.interview.businessprofile.config.Configuration.SUCCESS_STATUS;

@Service
public class ValidationServicesImpl implements ValidationServices {

    @Autowired
    private ValidationApi validationApi;

    @Override
    public ResponseEntity<ValidationServicesResponse> validate(BusinessProfile businessProfile, String product) {
        ValidationServicesResponse response = new ValidationServicesResponse();
        try {
            validationApi.validate(businessProfile, product);
            response.setMessage("Data is valid. validation done by " + product + " product");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (ValidationConfigurationException e) {
            response.setStatus(FAILURE_STATUS);
            response.setMessage("Error with validation regExp configuration. check resources/validationData.json for field configuration");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (InvalidDataException e) {
            response.setStatus(FAILURE_STATUS);
            response.setMessage(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (ValidationConfigurationNotFoundException e) {
            response.setStatus(FAILURE_STATUS);
            response.setMessage(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            response.setStatus(FAILURE_STATUS);
            response.setMessage(e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<BatchValidationResponse> validateInBatch(BatchValidationRequest batchValidationRequest) {
        BatchValidationResponse response = new BatchValidationResponse();
        try {
            response = validationApi.validateInBatch(batchValidationRequest);
            if (response.getStatus().equals(SUCCESS_STATUS)) {
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else {
                return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
