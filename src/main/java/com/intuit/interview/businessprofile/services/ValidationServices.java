package com.intuit.interview.businessprofile.services;

import com.intuit.interview.businessprofile.model.BatchValidationRequest;
import com.intuit.interview.businessprofile.model.BatchValidationResponse;
import com.intuit.interview.businessprofile.model.BusinessProfile;
import com.intuit.interview.businessprofile.model.ValidationServicesResponse;
import org.springframework.http.ResponseEntity;

public interface ValidationServices {
    ResponseEntity<ValidationServicesResponse> validate(BusinessProfile businessProfile, String product);

    ResponseEntity<BatchValidationResponse> validateInBatch(BatchValidationRequest batchValidationRequest);
}
