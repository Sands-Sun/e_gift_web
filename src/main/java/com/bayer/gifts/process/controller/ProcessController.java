package com.bayer.gifts.process.controller;

import com.bayer.gifts.process.common.R;
import com.bayer.gifts.process.form.GiftsTaskFrom;
import com.bayer.gifts.process.form.GivingGiftsForm;
import com.bayer.gifts.process.param.GiftsGroupParam;
import com.bayer.gifts.process.service.ProcessService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "process")
public class ProcessController {

    @Autowired
    ProcessService processService;


    @ApiOperation("获得任务信息")
    @RequestMapping(value = "/task/{id}", method = RequestMethod.GET)
    public R getTaskInfo(/*@Validated({AddGroup.class, Default.class})*/@PathVariable String taskId) {
//        param.setId(id);
//        giftsGroupService.updateGiftsGroup(param);
        return R.ok();
    }
    @ApiOperation("处理任务")
    @RequestMapping(value = "/task/handle/{id}", method = RequestMethod.POST)
    public R handleTask(@RequestBody GiftsTaskFrom form, @PathVariable String taskId) {
        form.setTaskId(taskId);
        processService.handleTask(form);
        return R.ok();
    }
}
