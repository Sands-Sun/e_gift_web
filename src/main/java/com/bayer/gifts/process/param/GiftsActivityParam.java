package com.bayer.gifts.process.param;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class GiftsActivityParam implements Serializable {
    private static final long serialVersionUID = -6335569994838351385L;

    private Long applicationId;
    private String sfProcessInsId;
    private List<String> sfActivityInsIds;
    private List<Long> sfUserIdSubmitters;
}
