package com.bayer.gifts.process.param;

import lombok.Data;

@Data
public class UserSearchParam extends FuzzySearchParam{

    private String companyCode;

}
