package com.bayer.gifts.process.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "manage")
public class ManageConfig {

    public static String APP_ADDRESS_URL;
    public static String UPLOAD_FILE_PATH;

    public static String TEMPLATE_BASE_PATH;
    public static int FUZZ_SEARCH_TOP_NUM;

    public static String FOLLOW_DATA_BASE_NAME;
    public static Double GIFT_UNIT_MIN_PRICE;
    public static Double GIFT_UNIT_MAX_PRICE;
    public static String HOSPITALITY_UNIT_MIN_PRICE;
    public static String HOSPITALITY_UNIT_MAX_PRICE;
    public static Double HOSPITALITY_TOTAL_MIN_PRICE;
    public static String DEFAULT_ROUTERS;
    public static List<EmployeeDivision> EMPLOYEE_DIVISIONS;


    public void setAppAddressUrl(String appAddressUrl) {
        APP_ADDRESS_URL = appAddressUrl;
    }

    public void setUploadFilePath(String uploadFilePath) {
        UPLOAD_FILE_PATH = uploadFilePath;
    }

    public void setTemplateBasePath(String templateBasePath) {
        TEMPLATE_BASE_PATH = templateBasePath;
    }

    public void setFuzzSearchTopNum(int fuzzSearchTopNum) {
        FUZZ_SEARCH_TOP_NUM = fuzzSearchTopNum;
    }

    public void setFollowDataBaseName(String followDataBaseName) {
        FOLLOW_DATA_BASE_NAME = followDataBaseName;
    }

    public void setGiftUnitMinPrice(Double giftUnitMinPrice) {
        GIFT_UNIT_MIN_PRICE = giftUnitMinPrice;
    }

    public void setGiftUnitMaxPrice(Double giftUnitMaxPrice) {
        GIFT_UNIT_MAX_PRICE = giftUnitMaxPrice;
    }

    public void setHospitalityTotalMinPrice(Double hospitalityTotalMinPrice) {
        HOSPITALITY_TOTAL_MIN_PRICE = hospitalityTotalMinPrice;
    }

    public void setHospitalityUnitMinPrice(String hospitalityUnitMinPrice) {
        HOSPITALITY_UNIT_MIN_PRICE = hospitalityUnitMinPrice;
    }

    public void setHospitalityUnitMaxPrice(String hospitalityUnitMaxPrice) {
        HOSPITALITY_UNIT_MAX_PRICE = hospitalityUnitMaxPrice;
    }

    public void setEmployeeDivisions(List<EmployeeDivision> employeeDivisions) {
        EMPLOYEE_DIVISIONS = employeeDivisions;
    }

    public void setDefaultRouters(String defaultRouters) {
        DEFAULT_ROUTERS = defaultRouters;
    }

    @Data
    public static class EmployeeDivision {
        private String companyCode;
        private String hierarchyMatch;
        private String division;
    }
}
