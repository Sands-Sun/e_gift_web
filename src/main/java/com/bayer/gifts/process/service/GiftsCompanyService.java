package com.bayer.gifts.process.service;

import com.bayer.gifts.process.entity.GiftsCompanyEntity;
import com.bayer.gifts.process.entity.GiftsPersonEntity;
import com.bayer.gifts.process.param.GiftCompanySearchParam;
import com.bayer.gifts.process.param.GiftPersonSearchParam;

import java.util.List;

public interface GiftsCompanyService {

    List<GiftsCompanyEntity> searchGiftCompanyList(GiftCompanySearchParam param);
    List<GiftsPersonEntity> searchGiftPersonList(GiftPersonSearchParam param);
}
