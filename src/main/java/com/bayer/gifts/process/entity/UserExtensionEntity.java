package com.bayer.gifts.process.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.bayer.gifts.process.common.Constant;
import com.bayer.gifts.process.config.ManageConfig;
import com.bayer.gifts.process.sys.entity.BaseEntity;
import com.bayer.gifts.process.sys.entity.RoleEntity;
import com.bayer.gifts.process.variables.GiftsApplyBaseVariable;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.identity.User;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Data
@Slf4j
@TableName("B_USER_EXTENSION")
public class UserExtensionEntity extends BaseEntity implements User,Serializable {
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
    private String division;

    @TableField(exist = false)
    private String supervisorCwid;

    @TableField(exist = false)
    private String supervisorEmail;

    @TableField(exist = false)
    private String supervisorName;

    @TableField(value = "OU_Description")
    private String OUDescription;

    @TableField(exist = false)
    private Boolean isDeptHead;

    @TableField(exist = false)
    private Boolean isCountryHead;

    @TableField(exist = false)
    private UserExtensionEntity supervisor;

    @TableField(exist = false)
    private List<RoleEntity> roles;

    @TableField(exist = false)
    private List<GiftsGroupEntity> groups;


//    public String getDivision() {
//        return findMatchDivision(this.companyCode, this.OUDescription);
//    }

    public void fillInDivision() {
        this.division = findMatchDivision(this.companyCode, this.OUDescription);
    }

    public String findMatchDivision(String companyCode, String divisionDesc) {
        log.info("companyCode >>>>> {},ouDescription  >>>>> {}",companyCode, divisionDesc);
        String division;
        List<ManageConfig.EmployeeDivision> collect =
                ManageConfig.EMPLOYEE_DIVISIONS.stream().filter(d -> d.getCompanyCode().equals(companyCode))
                        .collect(Collectors.toList());
        if(CollectionUtils.isEmpty(collect)){
            division = StringUtils.EMPTY;
        }else if(collect.size() == 1){
            division = collect.get(0).getDivision();
        }else {
            division = collect.stream().filter(d -> {
                String hierarchyMatch = d.getHierarchyMatch();
                log.info("hierarchyMatch >>>> {}", hierarchyMatch);
                return  StringUtils.isNotEmpty(hierarchyMatch) &&
                        Pattern.compile(hierarchyMatch)
                                .matcher(Objects.isNull(divisionDesc) ? StringUtils.EMPTY : divisionDesc).find();
            }).findFirst().get().getDivision();
        }
        log.info("After convert division  >>>>> {}", division);
        return division;
    }

    public boolean checkIsDepartmentHead() {
        this.isDeptHead = false;
        String bizGroup = GiftsApplyBaseVariable.getBizGroupByCompanyCode(companyCode);
        String divisionPrefix = Constant.GIFTS_BIZ_GROUP_BHC_NAME.equals(bizGroup) ? division : StringUtils.EMPTY;
        GiftsGroupEntity departmentHeadGroup =
                Constant.GIFTS_GROUP_MAP.get(bizGroup + divisionPrefix+ "_" + Constant.GIFTS_LEADERSHIP_DEPARTMENT_HEAD);
        if(Objects.nonNull(departmentHeadGroup) &&
                CollectionUtils.isNotEmpty(departmentHeadGroup.getUserToGroups()) &&
                departmentHeadGroup.getUserToGroups().stream().anyMatch(d -> d.getUserEmail().equals(this.email))){
            log.info("current user is department head >>>> {}" ,this.email);
            this.isDeptHead = true;
        }
        return isDeptHead;
    }

    public boolean checkIsCountryHead() {
        this.isCountryHead = false;
        String bizGroup = GiftsApplyBaseVariable.getBizGroupByCompanyCode(companyCode);
        GiftsGroupEntity countryHeadGroupByBiz = Constant.GIFTS_GROUP_MAP.get(bizGroup + "_" + Constant.GIFTS_LEADERSHIP_COUNTRY_HEAD);
        GiftsGroupEntity countryHeadGroup = Constant.GIFTS_GROUP_MAP.get(Constant.GIFTS_LEADERSHIP_COUNTRY_HEAD);
        boolean isCountryHeadGroupByBiz = Objects.nonNull(countryHeadGroupByBiz) &&
                CollectionUtils.isNotEmpty(countryHeadGroupByBiz.getUserToGroups()) &&
                countryHeadGroupByBiz.getUserToGroups().stream().anyMatch(d -> d.getUserEmail().equals(this.email));

        boolean isCountryHeadGroup = Objects.nonNull(countryHeadGroup) &&
                CollectionUtils.isNotEmpty(countryHeadGroup.getUserToGroups()) &&
                countryHeadGroup.getUserToGroups().stream().anyMatch(d -> d.getUserEmail().equals(this.email));
        log.info(">>> isCountryHeadGroupByBiz: {}, isCountryHeadGroup: {}",isCountryHeadGroupByBiz, isCountryHeadGroup);
        if(isCountryHeadGroup || isCountryHeadGroupByBiz){
            log.info("current user is country head >>>> {}" ,this.email);
            this.isCountryHead = true;
        }
        return isCountryHead;
    }



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
