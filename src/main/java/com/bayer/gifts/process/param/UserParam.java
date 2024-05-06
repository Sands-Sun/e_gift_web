package com.bayer.gifts.process.param;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class UserParam extends PageParam implements Serializable {

    private static final long serialVersionUID = 1620662892161825636L;

    private String chineseName;
    private String firstName;
    private String lastName;
    private String gender;
    private String email;
    private String cwid;
    private String companyCode;
    private String department;
    private List<OrderByParam> orders;
}
