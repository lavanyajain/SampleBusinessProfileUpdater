package com.intuit.interview.businessprofile.config;

import java.util.Arrays;
import java.util.List;

public class Configuration {
    public static final String FAILURE_STATUS = "FAILURE";
    public static final String SUCCESS_STATUS = "SUCCESS";
    public static final String QB_ACCOUNTING = "accounting";
    public static final String QB_PAYROLL = "payroll";
    public static final String QB_PAYMENTS = "payments";
    public static final String QB_TIMESHEET = "timesheet";
    public static final List<String> PRODUCTS = Arrays.asList(QB_ACCOUNTING, QB_PAYMENTS, QB_TIMESHEET, QB_PAYROLL);
}
