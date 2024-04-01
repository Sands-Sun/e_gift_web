package com.bayer.gifts.process.param;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OrderByParam {

    private String column;
    private String type;

   public enum Type{
        DESC,
        ASC
    }
}
