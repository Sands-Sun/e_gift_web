package com.bayer.gifts.process.param;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderByParam {

    private String column;
    private String type;

   public enum Type{
        DESC,
        ASC
    }
}
