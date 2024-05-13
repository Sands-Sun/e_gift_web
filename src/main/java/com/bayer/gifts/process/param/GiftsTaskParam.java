package com.bayer.gifts.process.param;

import com.bayer.gifts.process.config.ManageConfig;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class GiftsTaskParam extends GiftsApplicationParam implements Serializable {

    private static final long serialVersionUID = 4646246468816858872L;

    private List<String> groupIds;

    private String requestType;

    private String flowDbName = ManageConfig.FOLLOW_DATA_BASE_NAME;
}
