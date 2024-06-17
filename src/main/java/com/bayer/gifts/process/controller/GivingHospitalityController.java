package com.bayer.gifts.process.controller;

import com.bayer.gifts.process.common.Pagination;
import com.bayer.gifts.process.common.R;
import com.bayer.gifts.process.common.validator.group.AddGroup;
import com.bayer.gifts.process.entity.GivingHospApplicationEntity;
import com.bayer.gifts.process.form.GivingHospitalityFrom;
import com.bayer.gifts.process.param.GiftsApplicationParam;
import com.bayer.gifts.process.service.GivingHospitalityService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.groups.Default;

@Slf4j
@RestController
@RequestMapping(value = "hospitality/giving")
@Api(tags = "提供招待")
public class GivingHospitalityController extends AbstractController{

    @Autowired
    GivingHospitalityService givingHospitalityService;


    @ApiOperation("保存招待")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public R save(@Validated({AddGroup.class, Default.class})@RequestBody GivingHospitalityFrom hospitalityForm) {
        givingHospitalityService.saveGivingHospitality(hospitalityForm);
        return R.ok();
    }

    @ApiOperation("取消招待")
    @RequestMapping(value = "/cancel", method = RequestMethod.POST)
    public R cancel(@RequestBody GivingHospitalityFrom hospitalityForm) {
        givingHospitalityService.cancelGivingHospitality(hospitalityForm);
        return R.ok();
    }

    @ApiOperation("修改招待")
    @RequestMapping(value = "/update/{applicationId}", method = RequestMethod.POST)
    public R update(@RequestBody GivingHospitalityFrom hospitalityForm, @PathVariable Long applicationId) {
        hospitalityForm.setApplicationId(applicationId);
        givingHospitalityService.updateGivingHospitality(hospitalityForm);
        return R.ok();
    }

    @ApiOperation("复制招待")
    @RequestMapping(value = "/copy/{applicationId}", method = RequestMethod.POST)
    public R copy(@PathVariable Long applicationId) {
        return R.ok(givingHospitalityService.copyGivingHospitality(applicationId));
    }

    @ApiOperation("删除招待草稿")
    @RequestMapping(value = "/draft/delete/{applicationId}", method = RequestMethod.DELETE)
    public R delete(@PathVariable Long applicationId) {
        givingHospitalityService.deleteDraftGivingHospitality(applicationId);
        return R.ok();
    }

    @ApiOperation("根据Id获得招待")
    @RequestMapping(value = "/get/{applicationId}", method = RequestMethod.GET)
    public R<GivingHospApplicationEntity> get(@PathVariable Long applicationId) {
        return R.ok(givingHospitalityService.getGivingHospitalityByApplicationId(applicationId));
    }


    @ApiOperation("根据Id获得招待礼品历史记录")
    @RequestMapping(value = "/get/history/{applicationId}", method = RequestMethod.GET)
    public R<GivingHospApplicationEntity> getHistory(@PathVariable Long applicationId) {
        return R.ok(givingHospitalityService.getGivingHospitalityHistoryByApplicationId(applicationId));
    }

    @ApiOperation("获得招待列表")
    @RequestMapping(value = "/page", method = RequestMethod.POST)
    public R<Pagination<GivingHospApplicationEntity>> page(@RequestBody GiftsApplicationParam param) {
        return R.ok(givingHospitalityService.getGivingHospitalityApplicationList(param));
    }
}
