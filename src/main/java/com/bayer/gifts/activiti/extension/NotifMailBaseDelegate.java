package com.bayer.gifts.activiti.extension;

import com.bayer.gifts.process.common.Constant;
import com.bayer.gifts.process.entity.GiftsGroupEntity;
import com.bayer.gifts.process.entity.GiftsUserToGroupEntity;
import com.bayer.gifts.process.variables.GiftsApplyBaseVariable;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
public class NotifMailBaseDelegate {


    protected void setHistoryGroups(GiftsApplyBaseVariable variable,
                                  Pair<GiftsGroupEntity,List<GiftsUserToGroupEntity>> currentGroupUserPair) {
        if(Objects.isNull(currentGroupUserPair) || Objects.isNull(currentGroupUserPair.getKey())) {
            log.info("empty current group");
            return;
        }
        GiftsGroupEntity currentGroup = currentGroupUserPair.getKey();
        List<GiftsUserToGroupEntity> groupUsers = currentGroupUserPair.getRight();
        variable.setHisProcessGroups(currentGroup.getGroupCode(), groupUsers);
    }

    protected String getForApprovalStatus(String notifTypeValue, GiftsGroupEntity currentGroup) {
        log.info("set waiting for status....");
        if(Constant.GIFTS_REQUESTER.equals(notifTypeValue)){
            log.info("notify requester no need set status...");
            return null;
        }
        String groupFullName = currentGroup.getFullName();
        // Approve status
        return String.format("For %s Approval",groupFullName);
    }


    protected Pair<GiftsGroupEntity, List<GiftsUserToGroupEntity>> getGroupInfo(String typeValue, GiftsApplyBaseVariable variable) {
        log.info("notification type: {}", typeValue);
        Pair<GiftsGroupEntity,List<GiftsUserToGroupEntity>> pair = null;
        if(StringUtils.isEmpty(typeValue) || "NA".equals(typeValue)){
            log.info("empty typeValue: {}", typeValue);
            return null;
        }
        switch (typeValue) {
            case Constant.GIFTS_REQUESTER: {
                GiftsGroupEntity group = new GiftsGroupEntity();
                group.setFullName(StringUtils.EMPTY);
                group.setGroupCode(Constant.GIFTS_REQUESTER);
                GiftsUserToGroupEntity userToGroup = new GiftsUserToGroupEntity();
                userToGroup.setUserId(variable.getApplyForId());
                userToGroup.setUserEmail(variable.getApplyEmail());
                pair = Pair.of(group, Collections.singletonList(userToGroup));
                break;
            }
            case Constant.GIFTS_LEADERSHIP_LINE_MANAGER: {
                GiftsGroupEntity group = new GiftsGroupEntity();
                group.setFullName("Line Manager");
                group.setGroupCode(Constant.GIFTS_LEADERSHIP_LINE_MANAGER);
                GiftsUserToGroupEntity userToGroup = new GiftsUserToGroupEntity();
                userToGroup.setUserId(variable.getSupervisorId());
                userToGroup.setUserEmail(variable.getSupervisorMail());
                pair = Pair.of(group, Collections.singletonList(userToGroup));
                break;
            }
            case Constant.GIFTS_LEADERSHIP_SOC_GROUP:
                pair = variable.getScoGroupUserPair();
                break;
            case Constant.GIFTS_LEADERSHIP_DEPARTMENT_HEAD:
                pair = variable.getDepartmentHeadGroupUserPair();
                break;
            case Constant.GIFTS_LEADERSHIP_COUNTRY_HEAD:
                pair = variable.getCountryHeadGroupUserPair();
                break;
        }
        return pair;
    }
}
