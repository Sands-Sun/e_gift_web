package com.bayer.gifts.process.service.impl;

import com.alibaba.excel.support.ExcelTypeEnum;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bayer.gifts.excel.model.GiftsCompanyPersonModel;
import com.bayer.gifts.process.common.Constant;
import com.bayer.gifts.process.config.ManageConfig;
import com.bayer.gifts.process.dao.GiftsCompanyDao;
import com.bayer.gifts.process.dao.GiftsPersonDao;
import com.bayer.gifts.process.dao.GiftsRelationPersonDao;
import com.bayer.gifts.process.entity.GiftsCompanyEntity;
import com.bayer.gifts.process.entity.GiftsPersonEntity;
import com.bayer.gifts.process.entity.GiftsRelationPersonEntity;
import com.bayer.gifts.process.form.CompanyInfoForm;
import com.bayer.gifts.process.param.GiftsCompanySearchParam;
import com.bayer.gifts.process.param.GiftsPersonSearchParam;
import com.bayer.gifts.process.service.GiftsCompanyService;
import com.bayer.gifts.process.service.StorageService;
import com.bayer.gifts.process.sys.entity.FileMapEntity;
import com.bayer.gifts.process.sys.entity.FileUploadEntity;
import com.bayer.gifts.process.utils.EasyExcelUtil;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service("giftsCompanyService")
public class GiftsCompanyServiceImpl implements GiftsCompanyService {

    @Autowired
    GiftsCompanyDao giftsCompanyDao;

    @Autowired
    GiftsPersonDao giftsPersonDao;

    @Autowired
    GiftsRelationPersonDao giftsRelationPersonDao;

    @Autowired
    StorageService storageService;

    @Override
   public List<GiftsCompanyEntity> searchGiftCompanyList(GiftsCompanySearchParam param) {
        return giftsCompanyDao.queryFuzzyCompanyList(param);
    }

    @Override
    public List<GiftsCompanyEntity> getCompPersonByApplicationId(Long applicationId, String type) {
        return giftsCompanyDao.selectCompPersonByApplicationId(applicationId, type);
    }

    @Override
    public List<GiftsPersonEntity> searchGiftPersonList(GiftsPersonSearchParam param) {
        return giftsPersonDao.queryFuzzyPersonList(param);
    }


    public List<GiftsRelationPersonEntity> getGiftsRelationPersonByApplicationId(Long applicationId) {
        return giftsRelationPersonDao.selectList(Wrappers.<GiftsRelationPersonEntity>lambdaQuery()
                .eq(GiftsRelationPersonEntity::getApplicationId,applicationId));
    }

    @Override
    public void deleteGiftsRelationPersonByApplicationId(Long applicationId,String type){
        giftsRelationPersonDao.deleteByApplicationId(applicationId,type);
    }

    @Override
    public List<GiftsRelationPersonEntity> saveOrUpdateGiftsPerson(List<CompanyInfoForm> companyInfoFormList,
                                                                   Date currentDate, Long applicationId, Long userId,
                                                                   Long fileId, Double unitValue, String type) {
        log.info("save giving gifts person...");
        log.info("before merge company form attachment person size: {}",companyInfoFormList.size());
        mergeFromFileAttach(currentDate,applicationId,userId,fileId,companyInfoFormList);
        log.info("after merge company from attachment person size: {}", companyInfoFormList.size());

        if(CollectionUtils.isEmpty(companyInfoFormList)){
            return Collections.emptyList();
        }
        List<GiftsCompanyEntity> giftsCompanyEntityList = saveGiftsCompany(companyInfoFormList,userId);
        Map<String, List<GiftsPersonEntity>> personMap = companyInfoFormList.stream().collect(Collectors.toMap(
                c -> StringUtils.trim(c.getCompanyName()), CompanyInfoForm::getPersons, (oldValue, newValue) -> newValue));
        for(GiftsCompanyEntity companyEntity: giftsCompanyEntityList){
            List<GiftsPersonEntity> persons = personMap.get(companyEntity.getCompanyName());
            saveOrUpdateGiftsPerson(companyEntity,persons,userId);
        }

        List<GiftsPersonEntity> giftsPersonList = giftsCompanyEntityList.stream().
                flatMap(g -> g.getPersonList().stream()).collect(Collectors.toList());
        giftsRelationPersonDao.deleteByApplicationId(applicationId,type);
        GiftsRelationPersonEntity person;
        List<GiftsRelationPersonEntity> list = Lists.newArrayList();
        for(GiftsPersonEntity giftPerson : giftsPersonList){
            person = new GiftsRelationPersonEntity();
            person.setApplicationId(applicationId);
            person.setPersionId(giftPerson.getId());
            person.setPersonName(giftPerson.getPersonName());
            person.setCompanyName(giftPerson.getCompanyName());
            person.setType(type);
            person.setCreatedDate(currentDate);
            person.setLastModifiedDate(currentDate);
            person.setMoney(unitValue);
            person.setMarkDeleted(Constant.NO_EXIST_MARK);
            giftsRelationPersonDao.insert(person);
            list.add(person);
        }
        return list;
    }

    private void updateFileMap(Date currentDate,Long applicationId, Long userId, Long fileId) {
        log.info("update file map...");
        FileMapEntity fileMap = new FileMapEntity();
        fileMap.setApplicationId(applicationId);
        fileMap.setFileId(fileId);
        fileMap.setCreatedBy(String.valueOf(userId));
        fileMap.setCreatedDate(currentDate);
        fileMap.setLastModifiedBy(String.valueOf(userId));
        fileMap.setLastModifiedDate(currentDate);
        storageService.updateFileMap(fileMap);
    }

    private void mergeFromFileAttach(Date currentDate,Long applicationId,
                                     Long userId, Long fileId,
                                     List<CompanyInfoForm> companyInfoFormList) {
        if(Objects.isNull(fileId)){
            log.info("fileId is empty...");
            return;
        }
        FileUploadEntity fileUpload = storageService.getById(fileId);
        if(Objects.nonNull(fileUpload) && StringUtils.isNotEmpty(fileUpload.getFilePath())){
            File file = new File(ManageConfig.UPLOAD_FILE_PATH + fileUpload.getFilePath());
            if(!file.exists()){
                return;
            }
            Map<String, CompanyInfoForm> companyFromMap = companyInfoFormList.stream().collect(Collectors.toMap(
                    c -> StringUtils.trim(c.getCompanyName()), c -> c, (oldValue, newValue) -> newValue));
            Set<String> companyNames = companyFromMap.keySet();
            log.info("company names: {}", companyNames);
            try {
                EasyExcelUtil easyExcelUtil = new EasyExcelUtil();
                Pair<List<GiftsCompanyPersonModel>, Integer> pair =
                        easyExcelUtil.readExcel2007(
                                Files.newInputStream(file.toPath()), 1,1,
                                ExcelTypeEnum.XLSX, GiftsCompanyPersonModel.class);
                if(CollectionUtils.isNotEmpty(pair.getLeft())){
                    Map<String, List<GiftsPersonEntity>> personMapFromAttach = pair.getKey().stream().map(m -> {
                        GiftsPersonEntity person = new GiftsPersonEntity();
                        BeanUtils.copyProperties(m,person);
                        return person;
                    }).collect(Collectors.groupingBy(g -> StringUtils.trim(g.getCompanyName()), Collectors.toList()));

                    for(Map.Entry<String, List<GiftsPersonEntity>> entry : personMapFromAttach.entrySet()){
                        String companyName = entry.getKey();
                        List<GiftsPersonEntity> personsFromAttach = entry.getValue();
                        log.info("persons from attachment size: {}", personsFromAttach.size());
                        if(companyNames.contains(companyName)){
                            log.info("exist in request company from...");
                            CompanyInfoForm companyInfoForm = companyFromMap.get(companyName);
                            List<GiftsPersonEntity> personsFromRequest = companyInfoForm.getPersons();
                            log.info("persons from request size: {}", personsFromRequest.size());
                            List<GiftsPersonEntity> afterMergePersons = Stream.of(personsFromAttach,personsFromRequest)
                                    .distinct().flatMap(Collection::stream).collect(Collectors.toList());
                            log.info("after merge person size: {}", afterMergePersons.size());
                            companyInfoForm.setPersons(afterMergePersons);
                        }else {
                            log.info("not exist in request company from...");
                            CompanyInfoForm companyInfoForm = new CompanyInfoForm();
                            companyInfoForm.setCompanyName(companyName);
                            companyInfoForm.setPersons(personsFromAttach);
                            companyInfoFormList.add(companyInfoForm);
                        }
                    }

                }
            } catch (IOException e) {
                log.error("read company person excel issue, ",e);
            }

            fileUpload.setCreatedBy(String.valueOf(userId));
            fileUpload.setLastModifiedBy(String.valueOf(userId));
            fileUpload.setLastModifiedDate(currentDate);
            storageService.updateById(fileUpload);
            updateFileMap(currentDate,applicationId,userId,fileId);
        }
    }




    private void saveOrUpdateGiftsPerson(GiftsCompanyEntity giftsCompany,
                                                            List<GiftsPersonEntity> persons,
                                                            Long userId) {
        log.info("save or update gifts person...");
        Long giftsCompanyId = giftsCompany.getId();
        List<GiftsPersonEntity> result = Lists.newArrayList();
        List<String> personNameList = persons.stream().map(p -> StringUtils.trim(p.getPersonName())).distinct()
                .filter(StringUtils::isNotEmpty).collect(Collectors.toList());
        log.info("after trim persons: {}", personNameList);
        List<GiftsPersonEntity> giftsPersonList =
                giftsPersonDao.selectList(Wrappers.<GiftsPersonEntity>lambdaQuery().
                        eq(GiftsPersonEntity::getCompanyId, giftsCompanyId).
                        in(GiftsPersonEntity::getPersonName, personNameList));
        giftsPersonList.forEach(p -> p.setCompanyName(giftsCompany.getCompanyName()));
        List<String> giftsPersonNameList = giftsPersonList.stream().map(GiftsPersonEntity::getPersonName).collect(Collectors.toList());
        log.info("exist gifts person name: {}", giftsPersonNameList);
        List<GiftsPersonEntity> notMatchPersonList = persons.stream().filter(p -> !giftsPersonNameList.contains(p.getPersonName()))
                .collect(Collectors.toList());
        List<GiftsPersonEntity> hasMatchPersonList = persons.stream().filter(p -> giftsPersonNameList.contains(p.getPersonName()))
                .collect(Collectors.toList());
        List<GiftsPersonEntity> notMatchSaveList = batchSaveGiftsPerson(userId,giftsCompany,notMatchPersonList);
        List<GiftsPersonEntity> hasMatchUptList = batchUptGiftsPerson(userId,giftsPersonList,hasMatchPersonList);

        result.addAll(notMatchSaveList);
        result.addAll(hasMatchUptList);

        giftsCompany.setPersonList(result);
    }


    private List<GiftsCompanyEntity> saveGiftsCompany(List<CompanyInfoForm> companyInfoFormList,Long userId) {
        List<String> companyNames = companyInfoFormList.stream().map(c -> StringUtils.trim(c.getCompanyName())).distinct()
                .collect(Collectors.toList());
        log.info("companyName >>>> {}",companyNames);
        List<GiftsCompanyEntity> giftsCompanyList = giftsCompanyDao.selectList(Wrappers.<GiftsCompanyEntity>lambdaQuery()
                .in(GiftsCompanyEntity::getCompanyName, companyNames));
        List<String> existCompanyName = giftsCompanyList.stream().map(c -> StringUtils.trim(c.getCompanyName()))
                .collect(Collectors.toList());
        log.info("existCompanyName >>>> {}",existCompanyName);
        List<GiftsCompanyEntity> savedGiftsCompanyList = Lists.newArrayList();
        List<String> needSaveCompanyNames = companyNames.stream().filter(c -> !existCompanyName.contains(c))
                .collect(Collectors.toList());
        for(String companyName : needSaveCompanyNames){
            GiftsCompanyEntity giftsCompany = saveGiftsCompany(companyName, userId);
            savedGiftsCompanyList.add(giftsCompany);
        }
        savedGiftsCompanyList.addAll(giftsCompanyList);

        return savedGiftsCompanyList;
    }

    private List<GiftsPersonEntity> batchUptGiftsPerson(Long userId,
                                                        List<GiftsPersonEntity> giftsPersonList,
                                                        List<GiftsPersonEntity> hasMatchPersonList) {
        if(CollectionUtils.isEmpty(hasMatchPersonList)){
            log.info("don't have match gifts person...");
            return Collections.emptyList();
        }
        Map<String, GiftsPersonEntity> hasMatchPersonMap = hasMatchPersonList.stream().collect(Collectors.toMap(
                c -> StringUtils.trim(c.getPersonName()), p-> p, (oldValue, newValue) -> newValue));
        Date currentDate = new Date();
        for(GiftsPersonEntity person : giftsPersonList){
            String personName = person.getPersonName();
            String positionTitle = person.getPositionTitle();
            GiftsPersonEntity fromRequestPerson = hasMatchPersonMap.get(personName);
            if(!fromRequestPerson.getPositionTitle().trim().equals(positionTitle)){
                log.info("Not match position request: {}, history: {}", fromRequestPerson.getPositionTitle(), positionTitle);
               person.setPositionTitle(fromRequestPerson.getPositionTitle());
               person.setLastModifiedBy(userId);
               person.setLastModifiedDate(currentDate);
               giftsPersonDao.updateById(person);
            }
        }
        return giftsPersonList;
    }

    private List<GiftsPersonEntity> batchSaveGiftsPerson (Long userId,GiftsCompanyEntity giftsCompany,
                                                    List<GiftsPersonEntity> notMatchPersonList) {
        if(CollectionUtils.isEmpty(notMatchPersonList)){
            log.info("don't have not match gifts person...");
            return Collections.emptyList();
        }
        Date currentDate = new Date();
        GiftsPersonEntity giftsPerson;
        List<GiftsPersonEntity> notMatchSaveList = Lists.newArrayList();
        log.info("exist not match gifts person: {}", notMatchPersonList);
        for(GiftsPersonEntity notMatchPerson : notMatchPersonList){
            giftsPerson = new GiftsPersonEntity();
            String notMatchPersonName = notMatchPerson.getPersonName();
            giftsPerson.setPersonName(notMatchPersonName);
            giftsPerson.setCompanyId(giftsCompany.getId());
            giftsPerson.setCompanyName(giftsCompany.getCompanyName());
            giftsPerson.setMarkDeleted(Constant.NO_EXIST_MARK);
            giftsPerson.setDescription("description of :" + notMatchPersonName);
            giftsPerson.setPositionTitle(StringUtils.trim(notMatchPerson.getPositionTitle()));
            giftsPerson.setCreatedDate(currentDate);
            giftsPerson.setCreatedBy(userId);
            giftsPerson.setLastModifiedDate(currentDate);
            giftsPerson.setLastModifiedBy(userId);
            giftsPersonDao.insert(giftsPerson);
            notMatchSaveList.add(giftsPerson);
        }

        return notMatchSaveList;
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
