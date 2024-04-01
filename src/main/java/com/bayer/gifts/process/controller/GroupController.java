package com.bayer.gifts.process.controller;

import com.bayer.gifts.process.common.Pagination;
import com.bayer.gifts.process.common.R;
import com.bayer.gifts.process.entity.GiftsGroupEntity;
import com.bayer.gifts.process.param.GiftsGroupParam;
import com.bayer.gifts.process.service.GiftsGroupService;
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
@RequestMapping(value = "group")
public class GroupController extends AbstractController{

    @Autowired
    GiftsGroupService giftsGroupService;


    @ApiOperation("保存用户组")
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    public R save(/*@Validated({AddGroup.class, Default.class})*/ @RequestBody GiftsGroupParam param) {
        giftsGroupService.saveGiftsGroup(param);
        return R.ok();
    }

    @ApiOperation("修改用户组")
    @RequestMapping(value = "/update/{id}", method = RequestMethod.POST)
    public R update(/*@Validated({AddGroup.class, Default.class})*/ @RequestBody GiftsGroupParam param, @PathVariable Long id) {
        param.setId(id);
        giftsGroupService.updateGiftsGroup(param);
        return R.ok();
    }

    @ApiOperation("删除用户组")
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    public R delete(@PathVariable Long id){
        giftsGroupService.deleteGiftsGroup(id);
        return R.ok();
    }

    @ApiOperation("根据Id获得用户组")
    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    public R<GiftsGroupEntity> get(@PathVariable Long id) {
        return R.ok(giftsGroupService.getGiftsGroupById(id));
    }


    @ApiOperation("获得用户组列表")
    @RequestMapping(value = "/page", method = RequestMethod.POST)
    public R<Pagination<GiftsGroupEntity>> page(@RequestBody GiftsGroupParam param) {
        return R.ok(giftsGroupService.getGiftsGroupList(param));
    }
}
