package com.bayer.gifts.process.variables;

import com.bayer.gifts.process.common.Constant;
import com.bayer.gifts.process.entity.GiftsActivityBaseEntity;
import com.bayer.gifts.process.entity.GiftsGroupEntity;
import com.bayer.gifts.process.entity.GiftsRelationPersonEntity;
import com.bayer.gifts.process.entity.GiftsUserToGroupEntity;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

@Data
@Slf4j
public class GiftsApplyBaseVariable implements Serializable {

    private static final long serialVersionUID = -52659017265786469L;

    private String actionType;
    private Long applicationId;
    private Long applyForId;
    private String applyForName;
    private Long creatorId;
    private String creatorName;
    private String creatorEmail;
    private String applyEmail;
    private String applyDate;
    private Long supervisorId;
    private String supervisorName;
    private String supervisorMail;
    private String referenceNo;
    private String remark;
    private String companyCode;
    private String division;
    private String notifTypeValue;

    private boolean supervisorIsDeptHead = false;
    private boolean supervisorIsCountryHead = false;

    private List<String> signatureList;
    private List<String> remarkList;
    private List<String> copyToUserEmails;
    private Map<String,List<GiftsUserToGroupEntity>> hisProcessGroups = new HashMap<>();
    private Pair<GiftsGroupEntity,List<GiftsUserToGroupEntity>> scoGroupUserPair;
    private Pair<GiftsGroupEntity,List<GiftsUserToGroupEntity>> departmentHeadGroupUserPair;
    private Pair<GiftsGroupEntity,List<GiftsUserToGroupEntity>> countryHeadGroupUserPair;
    private Pair<GiftsGroupEntity,List<GiftsUserToGroupEntity>> currentGroupUserPair;
    private Pair<GiftsGroupEntity,List<GiftsUserToGroupEntity>> fromGroupUserPair;


    private List<String> lineManagerUsers = new ArrayList<>();
    private List<String> scoGroupUsers = new ArrayList<>();
    private List<String> departmentHeadGroupUsers = new ArrayList<>();
    private List<String> countryHeadGroupUsers = new ArrayList<>();

    private List<? extends GiftsActivityBaseEntity> activityList;


    public void setHisProcessGroups(String groupCode, List<GiftsUserToGroupEntity> groupUsers) {
        this.hisProcessGroups.put(groupCode,groupUsers);
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

    public String getRemarks() {
        return String.join("\n", remarkList);
    }

    public static String  getBizGroupByCompanyCode(String companyCode) {
        String bizGroup = StringUtils.EMPTY;
        if(Constant.GIFTS_LE_CODE_BCL_0813.equals(companyCode)){
            bizGroup = Constant.GIFTS_BIZ_GROUP_BCL_NAME;

        }else if(Constant.GIFTS_LE_CODE_BCS_1391.equals(companyCode)) {
            bizGroup = Constant.GIFTS_BIZ_GROUP_BCS_NAME;

        }else if(Constant.GIFTS_LE_CODE_SEM_2614.equals(companyCode)) {
            bizGroup = Constant.GIFTS_BIZ_GROUP_SEM_NAME;

        }else if(Constant.GIFTS_LE_CODE_BHC_0882.equals(companyCode)) {
            bizGroup = Constant.GIFTS_BIZ_GROUP_BHC_NAME;

        }else if(Constant.GIFTS_LE_CODE_CPL_1955.equals(companyCode)) {
            bizGroup = Constant.GIFTS_BIZ_GROUP_CPL_NAME;

        }else if(Constant.GIFTS_LE_CODE_DHG_1954.equals(companyCode)) {
            bizGroup = Constant.GIFTS_BIZ_GROUP_DHG_NAME;
        }
        return bizGroup;
    }

    public void fillInExtraVar(String companyCode, String orgBizGroup, GiftsGroupEntity deptHeadGroup) {
        log.info("fill in extra variable...");
        resetGroupUser();
        this.setLineManagerUsers(Collections.singletonList(String.valueOf(supervisorId)));
        String bizGroup = getBizGroupByCompanyCode(companyCode);
        log.info("current user companyCode: {}, orgBizGroup{}, bizGroup{}", companyCode,orgBizGroup,bizGroup);
        GiftsGroupEntity socGroup = Constant.GIFTS_GROUP_MAP.get(bizGroup + "_" + Constant.GIFTS_LEADERSHIP_SOC_GROUP);
        if(Objects.nonNull(socGroup) && CollectionUtils.isNotEmpty(socGroup.getUserToGroups())){
            List<GiftsUserToGroupEntity> socUserList = socGroup.getUserToGroups();
            List<String> socUsers = socUserList.stream()
                    .map(g -> String.valueOf(g.getUserId())).collect(Collectors.toList());
            log.info("soc group users: {}",socUsers);
            this.setScoGroupUsers(socUsers);
            this.setScoGroupUserPair(Pair.of(socGroup,socUserList));
        }
//        GiftsGroupEntity departmentHeadGroup = Constant.GIFTS_GROUP_MAP.get(bizGroup + "_DEPARTMENT_HEAD" );
        if(Objects.nonNull(deptHeadGroup) && CollectionUtils.isNotEmpty(deptHeadGroup.getUserToGroups())){
            List<GiftsUserToGroupEntity> departmentUserList = deptHeadGroup.getUserToGroups();
            List<String> departmentUsers = departmentUserList.stream()
                    .map(g -> String.valueOf(g.getUserId())).collect(Collectors.toList());
            log.info("department head group users: {}",departmentUsers);
            this.setDepartmentHeadGroupUsers(departmentUsers);
            this.setDepartmentHeadGroupUserPair(Pair.of(deptHeadGroup,departmentUserList));
        }
        GiftsGroupEntity countryHeadGroup = Constant.GIFTS_GROUP_MAP.get(bizGroup + "_" + Constant.GIFTS_LEADERSHIP_COUNTRY_HEAD);
        if(Objects.nonNull(countryHeadGroup) && CollectionUtils.isNotEmpty(countryHeadGroup.getUserToGroups())){
            List<GiftsUserToGroupEntity> countryHeadUserList = countryHeadGroup.getUserToGroups();
            List<String> countryHeadUsers = countryHeadUserList.stream()
                    .map(g -> String.valueOf(g.getUserId())).collect(Collectors.toList());
            log.info("country head group users: {}",countryHeadUsers);
            this.setCountryHeadGroupUsers(countryHeadUsers);
            this.setCountryHeadGroupUserPair(Pair.of(countryHeadGroup,countryHeadUserList));
        }
        checkSupervisor();
    }

    private void resetGroupUser() {
        log.info("reset group user...");
        log.info("before reset lineManagerUsers size: {}", this.lineManagerUsers.size());
        log.info("before reset scoGroupUsers size: {}, scoGroupUserPair : {}",
                this.scoGroupUsers.size(), this.scoGroupUserPair);
        log.info("before reset departmentHeadGroupUsers size: {}, departmentHeadGroupUserPair: {}",
                this.departmentHeadGroupUsers.size(), this.departmentHeadGroupUserPair);
        log.info("before reset countryHeadGroupUsers size: {}, countryHeadGroupUserPair: {}",
                this.countryHeadGroupUsers.size(), this.countryHeadGroupUserPair);

        this.lineManagerUsers.clear();
        this.scoGroupUsers.clear();
        this.scoGroupUserPair = null;

        this.departmentHeadGroupUsers.clear();
        this.departmentHeadGroupUserPair = null;

        this.countryHeadGroupUsers.clear();
        this.countryHeadGroupUserPair = null;

        log.info("after reset lineManagerUsers size: {}", this.lineManagerUsers.size());
        log.info("after reset scoGroupUsers size: {}", this.scoGroupUsers.size());
        log.info("after reset departmentHeadGroupUsers size: {}", this.departmentHeadGroupUsers.size());
        log.info("after reset countryHeadGroupUsers size: {}", this.countryHeadGroupUsers.size());
    }

    private void checkSupervisor() {
        supervisorIsDeptHead = this.departmentHeadGroupUsers.stream().anyMatch(d -> d.equals(String.valueOf(supervisorId)));
        supervisorIsCountryHead = this.countryHeadGroupUsers.stream().anyMatch(c -> c.equals(String.valueOf(supervisorId)));
        log.info("supervisorIsDeptHead: {}", supervisorIsDeptHead);
        log.info("supervisorIsCountryHead: {}", supervisorIsCountryHead);
    }
}
