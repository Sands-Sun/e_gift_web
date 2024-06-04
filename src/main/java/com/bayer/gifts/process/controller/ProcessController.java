package com.bayer.gifts.process.controller;

import com.bayer.gifts.process.common.R;
import com.bayer.gifts.process.form.GiftsTaskFrom;
import com.bayer.gifts.process.param.GiftsTaskParam;
import com.bayer.gifts.process.service.ProcessService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "process")
@Api(tags = "代办事项")
public class ProcessController {

    @Autowired
    ProcessService processService;


    @ApiOperation("获得代办任务列表")
    @RequestMapping(value = "/task/page", method = RequestMethod.POST)
    public R getTaskPage(@RequestBody GiftsTaskParam param) {
        log.info("get task page...");
        return R.ok(processService.getTaskList(param));
    }
    @ApiOperation("获得当前用户代办任务总数")
    @RequestMapping(value = "/task/current/run-count", method = RequestMethod.POST)
    public R getCurrRunTaskCount(@RequestBody GiftsTaskParam param) {
        return R.ok(processService.getCurrRunTaskCount(param));
    }

    @ApiOperation("处理任务")
    @RequestMapping(value = "/task/handle/{taskId}", method = RequestMethod.POST)
    public R handleTask(@RequestBody GiftsTaskFrom form, @PathVariable("taskId") String taskId) {
        form.setTaskId(taskId);
        processService.handleTask(form);
        return R.ok();
    }
}
