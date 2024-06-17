package com.bayer.gifts.process.controller;

import com.bayer.gifts.process.common.Constant;
import com.bayer.gifts.process.common.Pagination;
import com.bayer.gifts.process.common.R;
import com.bayer.gifts.process.common.validator.group.AddGroup;
import com.bayer.gifts.process.entity.GivingGiftsApplicationEntity;
import com.bayer.gifts.process.form.GivingGiftsForm;
import com.bayer.gifts.process.param.GiftsApplicationParam;
import com.bayer.gifts.process.service.GivingGiftsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.groups.Default;


@Slf4j
@RestController
@RequestMapping(value = "gifts/giving")
@Api(tags = "赠送礼品")
public class GivingGiftsController extends AbstractController{

    @Autowired
    GivingGiftsService givingGiftsService;


    @ApiOperation("保存赠送礼品")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public R save(@Validated({AddGroup.class, Default.class})@RequestBody GivingGiftsForm giftsForm) {
        givingGiftsService.saveGivingGifts(giftsForm);
        return R.ok();
    }

    @ApiOperation("取消赠送礼品")
    @RequestMapping(value = "/cancel", method = RequestMethod.POST)
    public R cancel(@RequestBody GivingGiftsForm giftsForm) {
        givingGiftsService.cancelGivingGifts(giftsForm);
        return R.ok();
    }

    @ApiOperation("修改赠送礼品")
    @RequestMapping(value = "/update/{applicationId}", method = RequestMethod.POST)
    public R update(@RequestBody GivingGiftsForm giftsForm, @PathVariable Long applicationId) {
        giftsForm.setApplicationId(applicationId);
        givingGiftsService.updateGivingGifts(giftsForm);
        return R.ok();
    }

    @ApiOperation("复制赠送礼品")
    @RequestMapping(value = "/copy/{applicationId}", method = RequestMethod.POST)
    public R copy(@PathVariable Long applicationId) {
        return R.ok(givingGiftsService.copyGivingGifts(applicationId));
    }

    @ApiOperation("删除接收礼品草稿")
    @RequestMapping(value = "/draft/delete/{applicationId}", method = RequestMethod.DELETE)
    public R delete(@PathVariable Long applicationId) {
        givingGiftsService.deleteDraftGivingGifts(applicationId);
        return R.ok();
    }

    @ApiOperation("根据Id获得赠送礼品")
    @RequestMapping(value = "/get/{applicationId}", method = RequestMethod.GET)
    public R<GivingGiftsApplicationEntity> get(@PathVariable Long applicationId) {
        return R.ok(givingGiftsService.getGivingGiftsByApplicationId(applicationId));
    }
    @ApiOperation("根据Id获得赠送礼品历史记录")
    @RequestMapping(value = "/get/history/{applicationId}", method = RequestMethod.GET)
    public R<GivingGiftsApplicationEntity> getHistory(@PathVariable Long applicationId) {
        return R.ok(givingGiftsService.getGivingGiftsHistoryByApplicationId(applicationId));
    }


    @ApiOperation("获得赠送礼品列表")
    @RequestMapping(value = "/page", method = RequestMethod.POST)
    public R<Pagination<GivingGiftsApplicationEntity>> page(@RequestBody GiftsApplicationParam param) {
        return R.ok(givingGiftsService.getGivingGiftsApplicationList(param));
    }

}
