package com.bayer.gifts.process.common.param;

import lombok.Data;

@Data
public class PageParam {

    private int pageSize = 5;
    private int currentPage = 1;
}
