package com.intuit.interview.businessprofile.api;

import com.intuit.interview.businessprofile.exception.SubscriptionUpdateException;
import com.intuit.interview.businessprofile.library.QueryExecutor;
import com.intuit.interview.businessprofile.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.intuit.interview.businessprofile.config.Configuration.FAILURE_STATUS;
import static com.intuit.interview.businessprofile.config.Configuration.SUCCESS_STATUS;

@Component
public class SubscriptionApi {

    @Autowired
    private ValidationApi validationApi;

    @Autowired
    private QueryExecutor queryExecutor;

    @Value("${business.profile.validator.validator.service.host}")
    private String HOST_URL;

    private BatchValidationResponse getBatchValidationResponse(SubscriptionRequest subscriptionRequest) {
        BatchValidationRequest batchValidationRequest = new BatchValidationRequest();
        batchValidationRequest.setBusinessProfile(subscriptionRequest.getBusinessProfile());
        batchValidationRequest.setProducts(subscriptionRequest.getProducts());
        return validationApi.validateInBatch(batchValidationRequest);
    }

    private List<String> checkValidationStatus(BatchValidationResponse validationResponse) {
        List<String> successfulValidations = new ArrayList<>();
        Set<String> keys = validationResponse.getValidationResponse().keySet();
        for (String key : keys)
            if (validationResponse.getValidationResponse().get(key).getStatus().equals(SUCCESS_STATUS)) {
                successfulValidations.add(key);
            } else {
                validationResponse.setStatus(FAILURE_STATUS);
            }
        return successfulValidations;
    }

    public List<String> getAllSubscribedProducts(String taxID) {
        String query = "select subscription.subscription from subscription INNER JOIN profile on profile.tax_id='" + taxID + "';";
        List<String> subscriptions = new ArrayList<>();
        ResultSet resultSet = QueryExecutor.executeQuery(query);
        try {
            while (resultSet.next()) {
                String product = resultSet.getString("SUBSCRIPTION");
                subscriptions.add(product);
            }
        } catch (Exception e) {
            throw new SubscriptionUpdateException(e.getMessage());
        }
        return subscriptions;
    }

    public void removeBusinessProfile(String taxID) {
        String query = "delete from profile where profile.tax_id='" + taxID + "';";
        QueryExecutor.executeUpdate(query);
    }

    private void updateLatestProductSubscriptions(String taxID, List<String> products) {
        for (String product : products) {
            String query = "insert into subscription(tax_id, subscription_id, subscription) values('" + taxID + "', " + "null, '" + product + "');";
            QueryExecutor.executeUpdate(query);
        }
    }

    private void insertBusinessProfile(SubscriptionRequest subscriptionRequest) {
        BusinessProfile businessProfile = subscriptionRequest.getBusinessProfile();
        String query = "insert into profile (tax_id, company_name, legal_name, address_line1, address_line2, address_city, address_state, address_zip, address_country, legal_address_line1, legal_address_line2, legal_address_city, legal_address_state, legal_address_zip, legal_address_country, email, website) " +
                "values ('" + businessProfile.getTaxID() + "' , '" + businessProfile.getCompanyName() + "', '" + businessProfile.getLegalName() + "', '" + businessProfile.getBusinessAddress().getLine1() + "', '" + businessProfile.getBusinessAddress().getLine2() + "', '" + businessProfile.getBusinessAddress().getCity() + "', '" + businessProfile.getBusinessAddress().getState() + "', '" + businessProfile.getBusinessAddress().getZip() + "', '" + businessProfile.getBusinessAddress().getCountry() + "', '" + businessProfile.getLegalAddress().getLine1() + "', '" + businessProfile.getLegalAddress().getLine2() + "', '" + businessProfile.getLegalAddress().getCity() + "', '" + businessProfile.getLegalAddress().getState() + "', '" + businessProfile.getLegalAddress().getZip() + "', '" + businessProfile.getLegalAddress().getCountry() + "', '" + businessProfile.getEmail() + "', '" + businessProfile.getWebsite() + "');";
        QueryExecutor.executeUpdate(query);
        updateLatestProductSubscriptions(businessProfile.getTaxID(), subscriptionRequest.getProducts());
    }

    public void createUpdate(SubscriptionRequest subscriptionRequest) {
        BusinessProfile profile = subscriptionRequest.getBusinessProfile();
        removeBusinessProfile(profile.getTaxID());
        insertBusinessProfile(subscriptionRequest);
    }

    public SubscriptionStatus subscribe(SubscriptionRequest subscriptionRequest) {
        SubscriptionStatus subscriptionResponse = new SubscriptionStatus();
        List<String> alreadySubscribed = getAllSubscribedProducts(subscriptionRequest.getBusinessProfile().getTaxID());
        List<String> subscriptionsRequested = subscriptionRequest.getProducts();
        List<String> allSubscriptions = new ArrayList<>(new HashSet<>(Stream.concat(alreadySubscribed.stream(), subscriptionsRequested.stream()).collect(Collectors.toList())));
        subscriptionRequest.setProducts(allSubscriptions);
        BatchValidationResponse validationResponse = getBatchValidationResponse(subscriptionRequest);
        List<String> validationList = checkValidationStatus(validationResponse);
        if (validationList.size() == subscriptionRequest.getProducts().size()) {
            subscriptionResponse.setBatchValidationResponse(validationResponse);
            subscriptionResponse.setStatus(SUCCESS_STATUS);
            subscriptionResponse.setMessage("Business profile is updated and also subscription list");
            createUpdate(subscriptionRequest);
        } else {
            subscriptionResponse.setBatchValidationResponse(validationResponse);
            subscriptionResponse.setMessage("Business profile is not updated. One of the products in subscription list invalidated this business profile.");
            subscriptionResponse.setStatus(FAILURE_STATUS);
        }
        return subscriptionResponse;
    }
}
