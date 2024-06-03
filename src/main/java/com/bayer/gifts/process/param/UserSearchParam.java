package com.bayer.gifts.process.param;

import com.bayer.gifts.process.common.param.FuzzySearchParam;
import lombok.Data;

@Data
public class UserSearchParam extends FuzzySearchParam {

    private String companyCode;

    private String division;
}
