package com.bayer.gifts.process.controller;

import com.bayer.gifts.process.common.R;
import com.bayer.gifts.process.entity.GiftsCompanyEntity;
import com.bayer.gifts.process.entity.GiftsPersonEntity;
import com.bayer.gifts.process.param.GiftCompanySearchParam;
import com.bayer.gifts.process.param.GiftPersonSearchParam;
import com.bayer.gifts.process.service.GiftsCompanyService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(value = "gifts/company")
public class GiftsCompanyController extends AbstractController{

    @Autowired
    GiftsCompanyService giftsCompanyService;

    @ApiOperation("模糊搜索公司列表")
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public R<List<GiftsCompanyEntity>> searchUserList(GiftCompanySearchParam searchParam) {
        return R.ok(giftsCompanyService.searchGiftCompanyList(searchParam));
    }

    @ApiOperation("模糊搜索公司人员")
    @RequestMapping(value = "/person/{companyId}/search", method = RequestMethod.GET)
    public R<List<GiftsPersonEntity>> searchUserList(@PathVariable("companyId") Long companyId,
                                                     GiftPersonSearchParam searchParam) {
        searchParam.setCompanyId(companyId);
        return R.ok(giftsCompanyService.searchGiftPersonList(searchParam));
    }
}
