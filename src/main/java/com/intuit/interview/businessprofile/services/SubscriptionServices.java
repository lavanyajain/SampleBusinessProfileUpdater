package com.intuit.interview.businessprofile.services;

import com.intuit.interview.businessprofile.model.SubscriptionRequest;
import com.intuit.interview.businessprofile.model.SubscriptionStatus;
import org.springframework.http.ResponseEntity;

public interface SubscriptionServices {
    ResponseEntity<SubscriptionStatus> subscribe(SubscriptionRequest subscriptionRequest);
    //ResponseEntity<SubscriptionResponse>
}
