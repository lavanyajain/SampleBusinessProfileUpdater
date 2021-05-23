package com.intuit.interview.businessprofile.api;

import com.intuit.interview.businessprofile.exception.InvalidDataException;
import com.intuit.interview.businessprofile.exception.ValidationConfigurationException;
import com.intuit.interview.businessprofile.model.BatchValidationRequest;
import com.intuit.interview.businessprofile.model.BatchValidationResponse;
import com.intuit.interview.businessprofile.model.BusinessProfile;
import com.intuit.interview.businessprofile.model.ValidationServicesResponse;
import com.intuit.interview.businessprofile.services.ValidationServices;
import com.intuit.interview.businessprofile.thread.ValidationThread;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static com.intuit.interview.businessprofile.config.Configuration.FAILURE_STATUS;

@Component
public class ValidationApi {

    private static final Logger logger = LoggerFactory.getLogger(ValidationApi.class);
    private final JSONParser parser = new JSONParser();
    @Value("${business.profile.validator.config.file}")
    private String validationConfigFile;
    @Autowired
    private ValidationServices validationServices;
    @Autowired
    private ValidationApi validationApi;
    @Value("${business.profile.validator.retry.count}")
    private Integer retryCount;

    private String getExceptionMessage(String message) {
        JSONObject errorResponse = null;
        try {
            errorResponse = (JSONObject) parser.parse(message.substring(message.indexOf("{"), message.indexOf("}") + 1));
        } catch (Exception e) {
            return message;
        }
        return String.valueOf(errorResponse.get("message"));
    }

    public BatchValidationResponse validateInBatch(BatchValidationRequest batchValidationRequest) {
        int index = 0;
        BatchValidationResponse batchValidationResponse = new BatchValidationResponse();
        HashMap<String, ValidationServicesResponse> status = new HashMap<>();
        Collection<Callable<ValidationServicesResponse>> tasks = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(4);
        List<String> products = batchValidationRequest.getProducts();
        ValidationServicesResponse futureObject = null;
        for (String product : products)
            tasks.add(new ValidationThread(batchValidationRequest.getBusinessProfile(), product));
        try {
            List<Future<ValidationServicesResponse>> results = executor.invokeAll(tasks);
            for (Future<ValidationServicesResponse> future : results) {
                try {
                    futureObject = future.get();
                    status.put(String.valueOf(batchValidationRequest.getProducts().get(index)), futureObject);
                } catch (Exception e) {
                    if (retryCount > 0) {
                        try {
                            futureObject = executor.submit(new ValidationThread(batchValidationRequest.getBusinessProfile(), batchValidationRequest.getProducts().get(index))).get();
                            status.put(String.valueOf(batchValidationRequest.getProducts().get(index)), futureObject);
                        } catch (Exception exc) {
                            retryCount--;
                            ValidationServicesResponse response = new ValidationServicesResponse();
                            response.setStatus(FAILURE_STATUS);
                            response.setMessage(getExceptionMessage(e.getMessage()));
                            status.put(String.valueOf(batchValidationRequest.getProducts().get(index)), response);
                        }
                    }
                    ValidationServicesResponse response = new ValidationServicesResponse();
                    response.setStatus(FAILURE_STATUS);
                    response.setMessage(getExceptionMessage(e.getMessage()));
                    status.put(String.valueOf(batchValidationRequest.getProducts().get(index)), response);
                    batchValidationResponse.setStatus(FAILURE_STATUS);
                    batchValidationResponse.setValidationResponse(status);
                }
                index++;
            }
        } catch (Exception e) {
            ValidationServicesResponse response = new ValidationServicesResponse();
            response.setStatus(FAILURE_STATUS);
            response.setMessage(getExceptionMessage(e.getMessage()));
            status.put(String.valueOf(batchValidationRequest.getProducts().get(index)), response);
            batchValidationResponse.setStatus(FAILURE_STATUS);
            batchValidationResponse.setValidationResponse(status);
        }
        batchValidationResponse.setValidationResponse(status);
        return batchValidationResponse;
    }

    private JSONObject readValidationConfiguration() {
        synchronized (this) {
            URL resource = getClass().getClassLoader().getResource(validationConfigFile);
            JSONObject validationConfiguration = null;
            FileReader fileReader = null;
            try {
                fileReader = new FileReader(resource.getFile());
                validationConfiguration = (JSONObject) parser.parse(fileReader);
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            } finally {
                try {
                    fileReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return (JSONObject) ((JSONObject) (validationConfiguration.get("validations"))).get("quickBooks");
        }
    }

    private String getFieldName(Field field) {
        return field.getName();
    }

    private boolean configValidation(Object response, String fieldName, JSONObject validationConfig) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        boolean optionalMatch = false;
        String subFieldName = null;
        if (response.getClass().getTypeName().equals("java.lang.String")) {
            if (validationConfig.get(fieldName) instanceof JSONArray) {
                optionalMatch = false;
                JSONArray validators = ((JSONArray) validationConfig.get(fieldName));
                for (int i = 0; i < validators.size(); i++)
                    if (String.valueOf(response).matches(validators.get(i).toString())) {
                        optionalMatch = true;
                        break;
                    }
                return optionalMatch;
            } else return String.valueOf(response).matches(validationConfig.get(fieldName).toString());
        } else {
            Field[] subFields = response.getClass().getDeclaredFields();
            for (Field subField : subFields) {
                subFieldName = getFieldName(subField);
                Method subFiledMethod = response.getClass().getMethod("get" + subFieldName.substring(0, 1).toUpperCase() + subFieldName.substring(1));
                Object res = subFiledMethod.invoke(response);
                boolean subFieldMatch = String.valueOf(res).matches(((JSONObject) validationConfig.get(fieldName)).get(subFieldName).toString());
                if (!subFieldMatch)
                    return false;
            }
        }
        return true;
    }

    public void validate(BusinessProfile businessProfile, String product) throws Exception {
        JSONObject validationConfig = (JSONObject) readValidationConfiguration().get(product);
        Field[] fields = businessProfile.getClass().getDeclaredFields();
        String fieldName;
        for (Field field : fields) {
            fieldName = getFieldName(field);
            try {
                Method getterMethod = businessProfile.getClass().getMethod("get" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1));
                Object response = getterMethod.invoke(businessProfile);
                if (!configValidation(response, fieldName, validationConfig)) {
                    throw new InvalidDataException("Invalid data for " + fieldName + " according to field configuration it should comply the regExp provided. This validation is by " + product + " QB application");
                }
            } catch (InvalidDataException e) {
                throw new InvalidDataException(e.getMessage());
            } catch (Exception e) {
                throw new ValidationConfigurationException("Error in validation configuration unable to setup configuration for all products. Check validationData.json for more information. " + e.getMessage());
            }
        }
    }
}
