package com.bayer.gifts.excel.model;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

@Data
public class HospCompanyPersonBaseModel {

    private String isGoSoc;
    private String isBayerCustomer;
    private String companyName;
    private String personName;
    private String positionTitle;

    public boolean notEmpty() {
        return StringUtils.isNotEmpty(companyName)
                && StringUtils.isNotEmpty(personName)
                && StringUtils.isNotEmpty(positionTitle);
    }
}
