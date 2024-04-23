package com.bayer.gifts.process.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bayer.gifts.process.common.Constant;
import com.bayer.gifts.process.dao.GiftsCompanyDao;
import com.bayer.gifts.process.dao.GiftsPersonDao;
import com.bayer.gifts.process.dao.GiftsRelationPersonDao;
import com.bayer.gifts.process.entity.GiftsCompanyEntity;
import com.bayer.gifts.process.entity.GiftsPersonEntity;
import com.bayer.gifts.process.entity.GiftsRelationPersonEntity;
import com.bayer.gifts.process.form.GivingGiftsForm;
import com.bayer.gifts.process.param.GiftCompanySearchParam;
import com.bayer.gifts.process.param.GiftPersonSearchParam;
import com.bayer.gifts.process.service.GiftsCompanyService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service("giftsCompanyService")
public class GiftsCompanyServiceImpl implements GiftsCompanyService {

    @Autowired
    GiftsCompanyDao giftsCompanyDao;

    @Autowired
    GiftsPersonDao giftsPersonDao;

    @Autowired
    GiftsRelationPersonDao giftsRelationPersonDao;

    @Override
   public List<GiftsCompanyEntity> searchGiftCompanyList(GiftCompanySearchParam param) {
        return giftsCompanyDao.queryFuzzyCompanyList(param);
    }

    @Override
    public List<GiftsPersonEntity> searchGiftPersonList(GiftPersonSearchParam param) {
        return giftsPersonDao.queryFuzzyPersonList(param);
    }


    public List<GiftsRelationPersonEntity> getGiftsRelationPersonByApplicationId(Long applicationId) {
        return giftsRelationPersonDao.selectList(Wrappers.<GiftsRelationPersonEntity>lambdaQuery()
                .eq(GiftsRelationPersonEntity::getApplicationId,applicationId));
    }

    @Override
    public void deleteGiftsRelationPersonByApplicationId(Long applicationId){
        giftsRelationPersonDao.deleteByApplicationId(applicationId);
    }

    @Override
    public List<GiftsRelationPersonEntity> saveOrUpdateGiftsPerson(Date currentDate, Long applicationId, Long userId,String personTitle,
                                                                   String company, List<String> personList, Double unitValue) {
        log.info("save giving gifts person...");
        String givenCompany = StringUtils.trim(company);
        GiftsCompanyEntity giftsCompany = giftsCompanyDao.selectOne(Wrappers.<GiftsCompanyEntity>lambdaQuery()
                .eq(GiftsCompanyEntity::getCompanyName,givenCompany));
        if(Objects.isNull(giftsCompany)){
            log.info("empty giving gifts company default save...");
            giftsCompany = saveGiftsCompany(givenCompany,userId);
        }
        List<GiftsPersonEntity> giftsPersonList = saveOrUpdateGiftsPerson(giftsCompany,personTitle,personList,userId);
        giftsRelationPersonDao.deleteByApplicationId(applicationId);
        GiftsRelationPersonEntity person;
        List<GiftsRelationPersonEntity> list = Lists.newArrayList();
        for(GiftsPersonEntity giftPerson : giftsPersonList){
            person = new GiftsRelationPersonEntity();
            person.setApplicationId(applicationId);
            person.setPersionId(giftPerson.getId());
            person.setPersonName(giftPerson.getPersonName());
            person.setCompanyName(giftsCompany.getCompanyName());
            person.setCreatedDate(currentDate);
            person.setLastModifiedDate(currentDate);
            person.setMoney(unitValue);
            person.setMarkDeleted(Constant.NO_EXIST_MARK);
            giftsRelationPersonDao.insert(person);
            list.add(person);
        }

        return list;
    }


    private List<GiftsPersonEntity> saveOrUpdateGiftsPerson(GiftsCompanyEntity giftsCompany,
                                                            String personTitle,
                                                            List<String> givenPersonNameList,
                                                            Long userId) {
        log.info("save or update gifts person...");
        Long giftsCompanyId = giftsCompany.getId();
        List<GiftsPersonEntity> result = Lists.newArrayList();
        log.info("before trim given persons: {}", givenPersonNameList);
        givenPersonNameList = givenPersonNameList.stream().map(String::trim).collect(Collectors.toList());
        log.info("after trim given persons: {}", givenPersonNameList);
        List<GiftsPersonEntity> giftsPersonList =
                giftsPersonDao.selectList(Wrappers.<GiftsPersonEntity>lambdaQuery().
                        eq(GiftsPersonEntity::getCompanyId, giftsCompanyId).
                        in(GiftsPersonEntity::getPersonName, givenPersonNameList));
        List<String> giftsPersonNameList = giftsPersonList.stream().map(GiftsPersonEntity::getPersonName).collect(Collectors.toList());
        log.info("exist gifts person name: {}", giftsPersonNameList);
        List<String> notMatchPersonList = givenPersonNameList.stream().filter(p -> !giftsPersonNameList.contains(p))
                .collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(notMatchPersonList)){
            Date currentDate = new Date();
            log.info("exist not match gifts person: {}", notMatchPersonList);
            GiftsPersonEntity giftsPerson;
            for(String notMatchPersonName : notMatchPersonList){
                giftsPerson = new GiftsPersonEntity();
                giftsPerson.setPersonName(notMatchPersonName);
                giftsPerson.setCompanyId(giftsCompanyId);
                giftsPerson.setMarkDeleted(Constant.NO_EXIST_MARK);
                giftsPerson.setDescription("description of :" + notMatchPersonName);
                giftsPerson.setPositionTitle(personTitle);
                giftsPerson.setCreatedDate(currentDate);
                giftsPerson.setCreatedBy(userId);
                giftsPerson.setLastModifiedDate(currentDate);
                giftsPerson.setLastModifiedBy(userId);
                giftsPersonDao.insert(giftsPerson);
                result.add(giftsPerson);
            }
        }
        result.addAll(giftsPersonList);

        return result;
    }


    private GiftsCompanyEntity saveGiftsCompany(String givenCompany,Long userId) {
        Date currentDate = new Date();
        GiftsCompanyEntity company = new GiftsCompanyEntity();
        company.setCompanyName(givenCompany);
        company.setDescription("description of :" + givenCompany);
        company.setMarkDeleted(Constant.NO_EXIST_MARK);
        company.setCreatedDate(currentDate);
        company.setCreatedBy(userId);
        company.setLastModifiedDate(currentDate);
        company.setLastModifiedBy(userId);
        giftsCompanyDao.insert(company);
        return company;
    }
}
