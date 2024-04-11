package com.bayer.gifts.process.service.impl;

import com.bayer.gifts.process.dao.GiftsCompanyDao;
import com.bayer.gifts.process.dao.GiftsPersonDao;
import com.bayer.gifts.process.entity.GiftsCompanyEntity;
import com.bayer.gifts.process.entity.GiftsPersonEntity;
import com.bayer.gifts.process.param.GiftCompanySearchParam;
import com.bayer.gifts.process.param.GiftPersonSearchParam;
import com.bayer.gifts.process.service.GiftsCompanyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service("giftsCompanyService")
public class GiftsCompanyServiceImpl implements GiftsCompanyService {

    @Autowired
    GiftsCompanyDao giftsCompanyDao;

    @Autowired
    GiftsPersonDao giftsPersonDao;

    @Override
   public List<GiftsCompanyEntity> searchGiftCompanyList(GiftCompanySearchParam param) {
        return giftsCompanyDao.queryFuzzyCompanyList(param);
    }

    @Override
    public List<GiftsPersonEntity> searchGiftPersonList(GiftPersonSearchParam param) {
        return giftsPersonDao.queryFuzzyPersonList(param);
    }
}
