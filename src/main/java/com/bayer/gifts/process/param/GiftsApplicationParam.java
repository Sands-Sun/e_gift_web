package com.bayer.gifts.process.param;

import com.alibaba.fastjson.annotation.JSONField;
import com.bayer.gifts.process.common.param.OrderByParam;
import com.bayer.gifts.process.common.param.PageParam;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class GiftsApplicationParam extends PageParam implements Serializable {

    private static final long serialVersionUID = 7998274425856519600L;
    private Long userId;
    private String userName;
    private String reference;
    private String companyCode;
    private String creator;
    private String applicant;
    private String cwid;
    private String department;
    @JSONField(format = "yyyy-MM-dd")
    private Date beginDate;
    @JSONField(format = "yyyy-MM-dd")
    private Date endDate;
    private List<String> status;
    private List<OrderByParam> orders;
}
