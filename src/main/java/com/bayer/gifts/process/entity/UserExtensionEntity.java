package com.bayer.gifts.process.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import org.activiti.engine.identity.Picture;
import org.activiti.engine.identity.User;
import org.activiti.engine.impl.persistence.entity.ByteArrayRef;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
@TableName("B_USER_EXTENSION")
public class UserExtensionEntity extends GiftsBaseEntity  implements User,Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(type = IdType.AUTO)
    private Long sfUserId;
    private String employeeId;
    private String bizGroup;
    private String companyCode;
    private String firstName;
    private String lastName;
    private String title;
    private String gender;
    private String maritalStatus;
    private String email;
    private Date joinDate;
    private Date birthday;
    private String birthPlace;
    private String homeAddress;
    private String employeeStatus;
    private String location;
    private String country;
    private String nationality;
    private String costCenter;
    private Integer orgId;
    private String positionId;
    private String orgTxt;
    private String jobTxt;
    private String positionTxt;
    private String isSupervisor;
    private String markDeleted;
    private String cwid;
    private String legalBoss;
    private String delegateBoss;
    private Long supervisorId;
    private String supervisorEmployeeId;
    private String supervisorLe;
    private Long staffTypeId;
    private String sapVendorNo;
    private String chineseName;
    private String assignment;
    private String employeeType;
    private String homeCountry;
    private String jobGrade;
    private String staffCategory;
    private Date joinLeDate;
    private BigDecimal lengthOfService;
    private Date dimissionDate;
    private String modifiedFlag;
    private String platinumEmployeeId;
    private Date probationEndDate;
    private String isDriver;
    private String isNeedCwt;
    private String freezeSup;
    private String notEligibleForOt;
    private String eligibleForTaxi;
    private String sendCarbonMailEnable;
    private String building;
    private String ipin;
    private Long hrId;
    private String wxLanguage;

    @TableField(exist = false)
    private UserExtensionEntity supervisor;

    @TableField(exist = false)
    private List<GiftsRoleEntity> roles;

    @TableField(exist = false)
    private List<GiftsGroupEntity> groups;


    @Override
    public String getId() {
        return this.cwid;
    }

    @Override
    public void setId(String s) {

    }


    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public void setPassword(String s) {

    }

    @Override
    public boolean isPictureSet() {
        return false;
    }

}
