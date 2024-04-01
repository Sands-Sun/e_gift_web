package com.bayer.gifts.process.mail.vo;

import com.bayer.gifts.process.variables.GivingGiftsApplyVariable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GivingGiftsNoticeMailVo extends NoticeMailVo{

    private GivingGiftsApplyVariable variable;


    public GivingGiftsNoticeMailVo(GivingGiftsApplyVariable variable) {
//        this.setMailTo();
        this.variable = variable;
    }
}
