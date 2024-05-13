package com.bayer.gifts.process.variables;

import com.bayer.gifts.process.common.Constant;
import com.bayer.gifts.process.entity.GiftsGroupEntity;
import com.bayer.gifts.process.entity.GiftsUserToGroupEntity;
import com.bayer.gifts.process.entity.GivingGiftsApplicationEntity;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Data
@Slf4j
public class GivingGiftsApplyVariable extends GiftsApplyBaseVariable implements Serializable {

    private static final long serialVersionUID = -4693004648737566736L;


    private String givenDate;
    private Double unitValue;
    private Integer volume;
    private Double totalValue;
    private String giftDescType;
    private String giftDesc;
    private String reasonType;
    private String reason;
//    private String signature;
    private String givenPersons;
    private String givenCompany;
    //是否是政府官员或国有企业员工
    private String isGoSoc;
    //接受者是否是拜耳现有客户
    private String isBayerCustomer;


    public boolean needDeptHeadApprove(String companyCode) {
        if(Constant.GIFTS_LE_CODE_BCL_0813.equals(companyCode)){
          boolean condition_one =  ("Yes".equals(isGoSoc) || "HCP".equals(isGoSoc))  || ("".equals(isGoSoc) && unitValue > 300);

        }
        return false;
    }
}
