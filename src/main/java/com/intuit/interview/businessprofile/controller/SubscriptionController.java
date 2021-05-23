package com.intuit.interview.businessprofile.controller;

import com.intuit.interview.businessprofile.model.SubscriptionRequest;
import com.intuit.interview.businessprofile.model.SubscriptionStatus;
import com.intuit.interview.businessprofile.services.SubscriptionServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/subscribe")
public class SubscriptionController {

    @Autowired
    private SubscriptionServices services;

    @PostMapping(produces = "application/json", consumes = "application/json")
    public ResponseEntity<SubscriptionStatus> subscribe(@RequestBody SubscriptionRequest subscriptionRequest) {
        return services.subscribe(subscriptionRequest);
    }
}
