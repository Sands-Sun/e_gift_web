package com.bayer.gifts.process.controller;

import com.bayer.gifts.process.common.Constant;
import com.bayer.gifts.process.common.Pagination;
import com.bayer.gifts.process.common.R;
import com.bayer.gifts.process.entity.ReceivingGiftsApplicationEntity;
import com.bayer.gifts.process.form.ReceivingGiftsForm;
import com.bayer.gifts.process.param.GiftsApplicationParam;
import com.bayer.gifts.process.service.ReceivingGiftsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "gifts/receiving")
@Api(tags = "接受礼品")
public class ReceivingGiftsController extends AbstractController{


    @Autowired
    ReceivingGiftsService receivingGiftsService;


    @ApiOperation("保存接收礼品")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public R save(@RequestBody ReceivingGiftsForm giftsForm) {
        receivingGiftsService.saveReceivingGifts(giftsForm);
        return R.ok();
    }
    @ApiOperation("取消接收礼品")
    @RequestMapping(value = "/cancel", method = RequestMethod.POST)
    public R cancel(@RequestBody ReceivingGiftsForm giftsForm) {
        receivingGiftsService.cancelReceivingGifts(giftsForm);
        return R.ok();
    }

    @ApiOperation("修改接收礼品")
    @RequestMapping(value = "/update/{applicationId}", method = RequestMethod.POST)
    public R update(@RequestBody ReceivingGiftsForm giftsForm, @PathVariable Long applicationId) {
        giftsForm.setApplicationId(applicationId);
        receivingGiftsService.updateReceivingGifts(giftsForm);
        return R.ok();
    }
    @ApiOperation("复制接受礼品")
    @RequestMapping(value = "/copy/{applicationId}", method = RequestMethod.POST)
    public R copy(@PathVariable Long applicationId) {
       return R.ok(receivingGiftsService.copyReceivingGifts(applicationId));
    }
    @ApiOperation("保存使用场景")
    @RequestMapping(value = "/save/user-case/{applicationId}", method = RequestMethod.POST)
    public R saveUseCase(@RequestBody ReceivingGiftsForm giftsForm, @PathVariable Long applicationId) {
        giftsForm.setApplicationId(applicationId);
        receivingGiftsService.saveUserCase(giftsForm);
        return R.ok();
    }

    @ApiOperation("删除接收礼品草稿")
    @RequestMapping(value = "/draft/delete/{applicationId}", method = RequestMethod.DELETE)
    public R delete(@PathVariable Long applicationId) {
        receivingGiftsService.deleteDraftReceivingGifts(applicationId);
        return R.ok();
    }


    @ApiOperation("根据Id获得接收礼品")
    @RequestMapping(value = "/get/{applicationId}", method = RequestMethod.GET)
    public R<ReceivingGiftsApplicationEntity> get(@PathVariable Long applicationId) {
        return R.ok(receivingGiftsService.getReceivingGiftsByApplicationId(applicationId));
    }

    @ApiOperation("获得接收礼品列表")
    @RequestMapping(value = "/page", method = RequestMethod.POST)
    public R<Pagination<ReceivingGiftsApplicationEntity>> page(@RequestBody GiftsApplicationParam param) {
        return R.ok(receivingGiftsService.getReceivingGiftsApplicationList(param));
    }

}
