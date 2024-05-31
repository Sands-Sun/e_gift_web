package com.bayer.gifts.process.entity;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Date;

@Data
public class GiftsBaseEntity {
    @JSONField(format = "yyyy-MM-dd")
    private Date createdDate;
    @JSONField(format = "yyyy-MM-dd")
    private Date lastModifiedDate;


}
