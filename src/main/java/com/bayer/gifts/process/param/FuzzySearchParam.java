package com.bayer.gifts.process.param;

import lombok.Data;

@Data
public class FuzzySearchParam {

    private int topNum = 10;
    private String keyword;
}
