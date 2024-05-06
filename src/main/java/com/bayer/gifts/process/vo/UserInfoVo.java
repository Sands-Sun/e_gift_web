package com.bayer.gifts.process.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class UserInfoVo implements Serializable {

    private static final long serialVersionUID = 5994309935143422976L;

    private Long sfUserId;
    private String employeeId;
    private String chineseName;
    private String firstName;
    private String lastName;
    private String title;
    private String gender;
    private String email;
    private String location;
    private String country;
    private String costCenter;
    private String orgTxt;
    private String cwid;
    private String companyCode;
    private Long supervisorId;
    private String supervisorEmployeeId;
    private String supervisorCwid;
    private String supervisorEmail;
    private String supervisorLe;
    private String isSupervisor;
}
