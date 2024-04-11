package com.bayer.gifts.process.param;

import lombok.Data;

@Data
public class GiftPersonSearchParam extends FuzzySearchParam{

    private Long companyId;
}
