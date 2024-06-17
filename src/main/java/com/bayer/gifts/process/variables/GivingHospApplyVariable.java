package com.bayer.gifts.process.variables;

import com.bayer.gifts.process.common.Constant;
import com.bayer.gifts.process.entity.GivingHospRelationPersonEntity;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;

import java.io.Serializable;
import java.util.List;

@Data
@Slf4j
public class GivingHospApplyVariable extends GiftsApplyBaseVariable implements Serializable {
    private static final long serialVersionUID = 3210271855831033171L;

    private String hospitalityDate;
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
    private String reason;
    private String reasonType;

    //是否是政府官员或国有企业员工
    private String isGoSoc;

    private List<GivingHospRelationPersonEntity> hospPersonList;


    public void setHospPersonList(List<GivingHospRelationPersonEntity> hospPersonList) {
        this.hospPersonList = hospPersonList;
        markIsGoSoc();
    }

    private void markIsGoSoc() {
        if(CollectionUtils.isNotEmpty(this.hospPersonList)
                && this.hospPersonList.stream().anyMatch(h -> Constant.YES_MARK.equalsIgnoreCase(h.getIsGoSoc()))){
            log.info("Hospitality any match Government employee");
            this.isGoSoc = Constant.YES_MARK;
        }
    }
}
