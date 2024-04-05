package com.bayer.gifts.process.variables;

import com.bayer.gifts.process.common.Constant;
import com.bayer.gifts.process.entity.GiftsGroupEntity;
import com.bayer.gifts.process.entity.GiftsUserToGroupEntity;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Data
@Slf4j
public class GivingGiftsApplyVariable implements Serializable {

    private static final long serialVersionUID = -4693004648737566736L;

    private String actionType;
    private Long applicationId;
    private Long applyForId;
    private String applyForName;
    private String creatorName;
    private String applyEmail;
    private String applyDate;
    private Long supervisorId;
    private String supervisorName;
    private String supervisorMail;
    private String givenDate;
    private Double unitValue;
    private Integer volume;
    private Double totalValue;
    private String giftDescType;
    private String giftDesc;
    private String reasonType;
    private String reason;
    private String referenceNo;
//    private String signature;
    private String remark;

    private String givenPersons;
    private String givenCompany;
    private String companyCode;

    //是否是政府官员或国有企业员工
    private String isGoSoc;
    //接受者是否是拜耳现有客户
    private String isBayerCustomer;

    private List<String> signatureList;

    private List<String> remarkList;

    List<Long> copyToUserIds;

//    private String SCOGroup;
//    private String DepartmentHeadGroup;
//    private String CountryHeadGroup;

    private List<String> scoGroupUsers;
    private List<String> departmentHeadGroupUsers;
    private List<String> countryHeadGroupUsers;

    private Pair<GiftsGroupEntity,List<GiftsUserToGroupEntity>> scoGroupUserPair;
    private Pair<GiftsGroupEntity,List<GiftsUserToGroupEntity>> departmentHeadGroupUserPair;
    private Pair<GiftsGroupEntity,List<GiftsUserToGroupEntity>> countryHeadGroupUserPair;
    private Pair<GiftsGroupEntity,List<GiftsUserToGroupEntity>> currentGroupUserPair;

    private Pair<GiftsGroupEntity,List<GiftsUserToGroupEntity>> fromGroupUserPair;

    public String getRemarks() {
       return String.join("\n", remarkList);
    }


    public List<GiftsUserToGroupEntity> getDepartmentHeadGroupUserList() {
        if(Objects.isNull(departmentHeadGroupUserPair)){
            return Collections.emptyList();
        }
        return departmentHeadGroupUserPair.getRight();
    }

    public List<GiftsUserToGroupEntity> getScoGroupUserList() {
        if(Objects.isNull(scoGroupUserPair)){
            return Collections.emptyList();
        }
        return scoGroupUserPair.getRight();
    }

    public List<GiftsUserToGroupEntity> getCountryHeadGroupUserList() {
        if(Objects.isNull(countryHeadGroupUserPair)){
            return Collections.emptyList();
        }
        return countryHeadGroupUserPair.getRight();
    }

    public GiftsGroupEntity getCurrentGiftGroup() {
        return currentGroupUserPair.getKey();
    }


    public String  getBizGroupByCompanyCode(String companyCode) {
        String bizGroup = StringUtils.EMPTY;
        if(Constant.GIFTS_LE_CODE_BCL_0813.equals(companyCode)){
            bizGroup = Constant.GIFTS_BIZ_GROUP_BCL_NAME;

        }else if(Constant.GIFTS_LE_CODE_BCS_1391.equals(companyCode)) {
            bizGroup = Constant.GIFTS_BIZ_GROUP_BCS_NAME;

        }else if(Constant.GIFTS_LE_CODE_BHC_0882.equals(companyCode)) {
            bizGroup = Constant.GIFTS_BIZ_GROUP_BHC_NAME;

        }else if(Constant.GIFTS_LE_CODE_CPL_1955.equals(companyCode)) {
            bizGroup = Constant.GIFTS_BIZ_GROUP_CPL_NAME;

        }else if(Constant.GIFTS_LE_CODE_DHG_1954.equals(companyCode)) {
            bizGroup = Constant.GIFTS_BIZ_GROUP_DHG_NAME;
        }
        return bizGroup;
    }


    public void fillInExtraVar(String companyCode,String orgBizGroup) {
        log.info("fill in extra variable...");
        String bizGroup = this.getBizGroupByCompanyCode(companyCode);
        log.info("current user companyCode: {}, orgBizGroup{}, bizGroup{}", companyCode,orgBizGroup,bizGroup);
        GiftsGroupEntity socGroup = Constant.GIFTS_GROUP_MAP.get(bizGroup + "_" + "SCO_GROUP");
        if(Objects.nonNull(socGroup) && CollectionUtils.isNotEmpty(socGroup.getUserToGroups())){
            List<GiftsUserToGroupEntity> socUserList = socGroup.getUserToGroups();
            List<String> socUsers = socUserList.stream()
                    .map(g -> String.valueOf(g.getUserId())).collect(Collectors.toList());
            log.info("soc group users: {}",socUsers);
            this.setScoGroupUsers(socUsers);
            this.setScoGroupUserPair(Pair.of(socGroup,socUserList));
        }
        GiftsGroupEntity departmentHeadGroup = Constant.GIFTS_GROUP_MAP.get("DEPARTMENT_HEAD_" + companyCode);
        if(Objects.nonNull(departmentHeadGroup) && CollectionUtils.isNotEmpty(departmentHeadGroup.getUserToGroups())){
            List<GiftsUserToGroupEntity> departmentUserList = departmentHeadGroup.getUserToGroups();
            List<String> departmentUsers = departmentUserList.stream()
                    .map(g -> String.valueOf(g.getUserId())).collect(Collectors.toList());
            log.info("department head group users: {}",departmentUsers);
            this.setDepartmentHeadGroupUsers(departmentUsers);
            this.setDepartmentHeadGroupUserPair(Pair.of(departmentHeadGroup,departmentUserList));
        }
        GiftsGroupEntity countryHeadGroup = Constant.GIFTS_GROUP_MAP.get("COUNTRY_HEAD_" + companyCode);
        if(Objects.nonNull(countryHeadGroup) && CollectionUtils.isNotEmpty(countryHeadGroup.getUserToGroups())){
            List<GiftsUserToGroupEntity> countryHeadUserList = countryHeadGroup.getUserToGroups();
            List<String> countryHeadUsers = countryHeadUserList.stream()
                    .map(g -> String.valueOf(g.getUserId())).collect(Collectors.toList());
            log.info("country head group users: {}",countryHeadUsers);
            this.setCountryHeadGroupUsers(countryHeadUsers);
            this.setCountryHeadGroupUserPair(Pair.of(countryHeadGroup,countryHeadUserList));
        }

    }
}
