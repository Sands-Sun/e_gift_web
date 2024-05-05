package com.bayer.gifts.process.form;

import lombok.Data;

import javax.validation.constraints.Pattern;

@Data
public class GiftsTaskFrom {

    private Long userId;
    private String taskId;
    private Long applicationId;
    @Pattern(regexp ="Giving Gifts|Giving Hospitality")
    private String processType;
    @Pattern(regexp ="Approved|Rejected")
    private String approve;
    private String comment;
}
