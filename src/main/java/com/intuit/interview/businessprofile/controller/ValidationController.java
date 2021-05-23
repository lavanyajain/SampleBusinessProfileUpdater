package com.intuit.interview.businessprofile.controller;

import com.intuit.interview.businessprofile.model.BatchValidationRequest;
import com.intuit.interview.businessprofile.model.BatchValidationResponse;
import com.intuit.interview.businessprofile.model.BusinessProfile;
import com.intuit.interview.businessprofile.model.ValidationServicesResponse;
import com.intuit.interview.businessprofile.services.ValidationServices;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import static com.intuit.interview.businessprofile.config.Configuration.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/validate")
public class ValidationController {
    private static final Logger logger = LoggerFactory.getLogger(ValidationController.class);

    @Autowired
    private ValidationServices services;

    @PostMapping(value = "/accounting", produces = "application/json", consumes = "application/json")
    public ResponseEntity<ValidationServicesResponse> validateAccounting(@RequestBody BusinessProfile businessProfile) {
        return services.validate(businessProfile, QB_ACCOUNTING);
    }

    @PostMapping(value = "/payroll", produces = "application/json", consumes = "application/json")
    public ResponseEntity<ValidationServicesResponse> validatePayRoll(@RequestBody BusinessProfile businessProfile) {
        return services.validate(businessProfile, QB_PAYROLL);
    }

    @PostMapping(value = "/payments", produces = "application/json", consumes = "application/json")
    public ResponseEntity<ValidationServicesResponse> validatePayments(@RequestBody BusinessProfile businessProfile) {
        return services.validate(businessProfile, QB_PAYMENTS);
    }

    @PostMapping(value = "/timesheet", produces = "application/json", consumes = "application/json")
    public ResponseEntity<ValidationServicesResponse> validateTimeSheet(@RequestBody BusinessProfile businessProfile) {
        return services.validate(businessProfile, QB_TIMESHEET);
    }

    @PostMapping
    public ResponseEntity<BatchValidationResponse> validateInBatch(@RequestBody BatchValidationRequest batchValidationRequest) {
        return services.validateInBatch(batchValidationRequest);
    }

    @GetMapping
    public ResponseEntity<String> getSomeThins() {
        return new ResponseEntity<>("Lavanya", HttpStatus.OK);
    }
}
