package com.bayer.gifts.process.sys.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;

@Data
public class BaseEntity {
    @JSONField(format = "yyyy-MM-dd")
    private Date createdDate;
    @JSONField(format = "yyyy-MM-dd")
    private Date lastModifiedDate;


}
