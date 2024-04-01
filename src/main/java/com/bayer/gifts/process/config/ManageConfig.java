package com.bayer.gifts.process.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "manage")
public class ManageConfig {

    public static String SEND_EMAIL_URL;
    public static Double GIVING_UNIT_MIN_PRICE;
    public static Double GIVING_UNIT_MAX_PRICE;
    public static Double HOSPITALITY_TOTAL_MIN_PRICE;

    public void setSendEmailUrl(String sendEmailUrl) {
        SEND_EMAIL_URL = sendEmailUrl;
    }

    public void setGivingUnitMinPrice(Double givingUnitMinPrice) {
        GIVING_UNIT_MIN_PRICE = givingUnitMinPrice;
    }

    public void setGivingUnitMaxPrice(Double givingUnitMaxPrice) {
        GIVING_UNIT_MAX_PRICE = givingUnitMaxPrice;
    }

    public void setHospitalityTotalMinPrice(Double hospitalityTotalMinPrice) {
        HOSPITALITY_TOTAL_MIN_PRICE = hospitalityTotalMinPrice;
    }
}
