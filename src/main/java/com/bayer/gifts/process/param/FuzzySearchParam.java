package com.bayer.gifts.process.param;

import com.bayer.gifts.process.config.ManageConfig;
import lombok.Data;

@Data
public class FuzzySearchParam {

    private int topNum = ManageConfig.FUZZ_SEARCH_TOP_NUM;
    private String keyword;
}
