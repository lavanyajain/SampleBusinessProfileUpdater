package com.intuit.interview.businessprofile.services;

import com.intuit.interview.businessprofile.api.SubscriptionApi;
import com.intuit.interview.businessprofile.model.SubscriptionRequest;
import com.intuit.interview.businessprofile.model.SubscriptionStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import static com.intuit.interview.businessprofile.config.Configuration.FAILURE_STATUS;
import static com.intuit.interview.businessprofile.config.Configuration.SUCCESS_STATUS;

@Service
public class SubscriptionServicesImpl implements SubscriptionServices {
    @Autowired
    private SubscriptionApi subscriptionApi;

    @Override
    public ResponseEntity<SubscriptionStatus> subscribe(SubscriptionRequest subscriptionRequest) {
        SubscriptionStatus subscriptionStatus = subscriptionApi.subscribe(subscriptionRequest);
        if (subscriptionStatus.getStatus().equals(SUCCESS_STATUS)) {
            subscriptionStatus.setTexID(subscriptionRequest.getBusinessProfile().getTaxID());
            return new ResponseEntity<>(subscriptionStatus, HttpStatus.OK);
        } else {
            subscriptionStatus.setTexID(subscriptionRequest.getBusinessProfile().getTaxID());
            subscriptionStatus.setStatus(FAILURE_STATUS);
            subscriptionStatus.setMessage("Error while updating business profile one/more of the subscribed products marked this profile as invalid. Check subscription list to know more information on products");
            return new ResponseEntity<>(subscriptionStatus, HttpStatus.FORBIDDEN);
        }
    }
}
