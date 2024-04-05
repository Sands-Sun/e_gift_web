package com.bayer.gifts.process.common;

import com.bayer.gifts.process.entity.GiftsGroupEntity;
import com.bayer.gifts.process.mail.entity.MailPolicy;
import com.bayer.gifts.process.mail.entity.MailTemplate;

import java.util.HashMap;
import java.util.Map;

public class Constant {
    public static final String GIVING_GIFTS_TYPE = "Gifts";
    public static final String GIVING_HOSPITALITY_TYPE = "Hospitality";

//    public static final String GIFTS_SUBMIT_ACTION ="submited";
//    public static final String GIFTS_APPROVE_ACTION="approved";
//    public static final String GIFTS_DRAFT_ACTION="save_drafted";
//    public static final String GIFTS_REJECT_ACTION="rejected";
//    public static final String GIFTS_CANCEL_ACTION="cancelled";
//    public static final String GIFTS_DELETE_ACTION="deleted";

    public static final String GIFTS_DRAFT_TYPE ="Draft";
    public static final String GIFT_SUBMIT_TYPE="Submit";
    public static final String GIFTS_APPROVE_TYPE="Approved";
    public static final String GIFTS_REJECTED_TYPE ="Rejected";
    public static final String GIFTS_CANCELLED_TYPE ="Cancelled";
    public static final String GIFTS_COMPLETED_TYPE ="Completed";
    public static final String GIFTS_DOCUMENTED_TYPE ="Documented";

    public static final String GIFTS_COPY_TYPE = "Copy";

    public static final String GIFTS_LE_CODE_BCL_0813 = "0813";
    public static final String GIFTS_LE_CODE_BCS_1391 = "1391";
    public static final String GIFTS_LE_CODE_BHC_0882 = "0882";
    public static final String GIFTS_LE_CODE_CPL_1955 = "1955";
    public static final String GIFTS_LE_CODE_DHG_1954 = "1954";

    public static final String GIFTS_BIZ_GROUP_BCL_NAME = "BCL";
    public static final String GIFTS_BIZ_GROUP_BCS_NAME = "BCS";
    public static final String GIFTS_BIZ_GROUP_BHC_NAME = "BHC";
    public static final String GIFTS_BIZ_GROUP_CPL_NAME = "CPL";
    public static final String GIFTS_BIZ_GROUP_DHG_NAME = "DHG";


    public static final String GIFTS_REQUESTER = "REQUESTER";
    public static final String GIFTS_LEADERSHIP_LINE_MANAGER = "LINE_MANAGER";
    public static final String GIFTS_LEADERSHIP_SOC_GROUP = "SCO_GROUP";
    public static final String GIFTS_LEADERSHIP_DEPARTMENT_HEAD = "DEPARTMENT_HEAD";
    public static final String GIFTS_LEADERSHIP_COUNTRY_HEAD = "COUNTRY_HEAD";


    public static final String GIVING_GIFTS_REQUEST_TYPE = "Giving Gifts";
    public static final String EXIST_MARK= "Y";
    public static final String NO_EXIST_MARK= "N";

    public static Map<String,Map<String, MailTemplate>> MAIL_TEMPLATE_MAP = new HashMap<>();

    public static Map<String, GiftsGroupEntity> GIFTS_GROUP_MAP = new HashMap<>();
}
