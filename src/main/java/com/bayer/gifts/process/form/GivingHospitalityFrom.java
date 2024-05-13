package com.bayer.gifts.process.form;

import com.bayer.gifts.process.entity.HospitalityInviteeEntity;
import com.bayer.gifts.process.entity.HospitalityPersonEntity;
import lombok.Data;

import java.util.List;

@Data
public class GivingHospitalityFrom extends FormBase {

    //请描述招待活动
    private String hospitalityType;

    //估计的人均费用
    private Double expensePerHead;

    //受邀人人数
    private Integer headCount;

    // 估计的总费用
    private Double estimatedTotalExpense;

    //地点
    private String hospPlace;

    private List<GiftCompInfoForm> companyList;
}

