package com.bayer.gifts.process.controller;

import com.bayer.gifts.process.common.Pagination;
import com.bayer.gifts.process.common.R;
import com.bayer.gifts.process.entity.ReceivingHospApplicationEntity;
import com.bayer.gifts.process.param.GiftsApplicationParam;
import com.bayer.gifts.process.service.ReceivingHospitalityService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(value = "hospitality/receiving")
@Api(tags = "接受招待")
public class ReceivingHospitalityController extends AbstractController{

    @Autowired
    ReceivingHospitalityService receivingHospitalityService;


    @ApiOperation("根据Id获得接受招待")
    @RequestMapping(value = "/get/history/{applicationId}", method = RequestMethod.GET)
    public R<ReceivingHospApplicationEntity> get(@PathVariable Long applicationId) {
        return R.ok(receivingHospitalityService.getReceivingHospitalityHistoryByApplicationId(applicationId));
    }


    @ApiOperation("获得接受招待列表")
    @RequestMapping(value = "/page", method = RequestMethod.POST)
    public R<Pagination<ReceivingHospApplicationEntity>> page(@RequestBody GiftsApplicationParam param) {
        return R.ok(receivingHospitalityService.getReceivingHospitalityApplicationList(param));
    }
}
