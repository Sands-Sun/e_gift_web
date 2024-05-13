package com.bayer.gifts.process.service;

import com.bayer.gifts.process.common.Pagination;
import com.bayer.gifts.process.form.GiftsTaskFrom;
import com.bayer.gifts.process.param.GiftsTaskParam;
import com.bayer.gifts.process.vo.TaskInstanceVo;

import java.util.List;

public interface ProcessService {

    void handleTask(GiftsTaskFrom form);
    Pagination<TaskInstanceVo> getTaskList(GiftsTaskParam param);

    List<TaskInstanceVo> getHistoricAndExecActTaskList(Long applicationId,String processInsId, String processType);

}
