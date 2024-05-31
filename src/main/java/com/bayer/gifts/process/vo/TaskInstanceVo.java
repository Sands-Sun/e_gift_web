package com.bayer.gifts.process.vo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class TaskInstanceVo implements Serializable {

    private static final long serialVersionUID = -7653721638229782883L;

    private String sfProcessInsId;
    private String taskId;
    private String applicationId;
    private String sfUserIdCreator;
    private Long sfUserIdAppliedFor;
    private String sfUserAppliedName;
    private String sfUserAppliedCwid;
    private String sfUserAppliedEmail;
    private String reference;
    @JSONField(format = "yyyy-MM-dd")
    private Date applicationDate;
    private String requestType;
    private String employeeLe;
    private String department;
    private String status;
}
