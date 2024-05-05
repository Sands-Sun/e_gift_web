package com.bayer.gifts.process.param;

import lombok.Data;

@Data
public class GiftsPersonSearchParam extends FuzzySearchParam{

    private Long companyId;
}
