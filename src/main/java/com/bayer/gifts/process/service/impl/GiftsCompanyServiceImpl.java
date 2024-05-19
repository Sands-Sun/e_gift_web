package com.bayer.gifts.process.service.impl;

import com.alibaba.excel.support.ExcelTypeEnum;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bayer.gifts.excel.model.GiftsCompanyPersonModel;
import com.bayer.gifts.excel.model.HospCompanyPersonModel;
import com.bayer.gifts.process.common.BaseException;
import com.bayer.gifts.process.common.Constant;
import com.bayer.gifts.process.config.ManageConfig;
import com.bayer.gifts.process.dao.GiftsCompanyDao;
import com.bayer.gifts.process.dao.GiftsPersonDao;
import com.bayer.gifts.process.dao.GiftsRelationPersonDao;
import com.bayer.gifts.process.dao.HospRelationPersonDao;
import com.bayer.gifts.process.entity.*;
import com.bayer.gifts.process.form.GiftCompInfoForm;
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
    HospRelationPersonDao hospRelationPersonDao;

    @Autowired
    StorageService storageService;

    @Override
   public List<GiftsCompanyEntity> searchGiftCompanyList(GiftsCompanySearchParam param) {
        return giftsCompanyDao.queryFuzzyCompanyList(param);
    }

    @Override
    public List<GiftsCompanyEntity> getCompPersonByApplicationId(Long applicationId,String category, String type) {
        return giftsCompanyDao.selectCompPersonByApplicationId(applicationId, category,type);
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
    public void deleteGiftsRelationPersonByApplicationId(Long applicationId,String category,String type){
        if(Constant.HOSPITALITY_TYPE.equals(category)){
            hospRelationPersonDao.deleteByApplicationId(applicationId,type);
        }else if(Constant.GIFTS_TYPE.equals(category)){
            giftsRelationPersonDao.deleteByApplicationId(applicationId,type);
        }
    }
    @Override
    public List<HospitalityRelationPersonEntity> saveOrUpdateHospPerson(Long applicationId,Date currentDate,String type,
                                                                        Double expensePerHead,
                                                                        List<GiftsPersonEntity> giftsPersonList) {
        HospitalityRelationPersonEntity person;
        List<HospitalityRelationPersonEntity> list = Lists.newArrayList();
        for(GiftsPersonEntity giftPerson : giftsPersonList){
            person = new HospitalityRelationPersonEntity();
            person.setApplicationId(applicationId);
            person.setPersionId(giftPerson.getId());
            person.setPositionTitle(giftPerson.getPositionTitle());
            person.setPersonName(giftPerson.getPersonName());
            person.setCompanyName(giftPerson.getCompanyName());
            person.setIsGoSoc(giftPerson.getIsGoSoc());
            person.setIsBayerCustomer(giftPerson.getIsBayerCustomer());
            person.setType(type);
            person.setCreatedDate(currentDate);
            person.setLastModifiedDate(currentDate);
            person.setMoney(expensePerHead);
            person.setMarkDeleted(Constant.NO_EXIST_MARK);
            hospRelationPersonDao.insert(person);
            list.add(person);
        }
        return list;
    }

    @Override
    public List<HospitalityRelationPersonEntity> saveOrUpdateHospPerson(List<GiftCompInfoForm> giftCompInfoFormList,
                                                                        UserExtensionEntity user,
                                                                        Date currentDate, Long applicationId,
                                                                        Double expensePerHead,
                                                                        Long fileId, String type) {
        log.info("save giving gifts person...");
        log.info("before merge company form attachment person size: {}", giftCompInfoFormList.size());
        mergeHospFromFileAttach(currentDate,applicationId,fileId,user, giftCompInfoFormList);
        log.info("after merge company from attachment person size: {}", giftCompInfoFormList.size());
        if(CollectionUtils.isEmpty(giftCompInfoFormList)){
            return Collections.emptyList();
        }
        List<GiftsPersonEntity> giftsPersonList = saveGiftPerson(giftCompInfoFormList, user.getSfUserId());
        hospRelationPersonDao.deleteByApplicationId(applicationId,type);
        List<HospitalityRelationPersonEntity> relationPersonList =
                saveOrUpdateHospPerson(applicationId,currentDate,type,expensePerHead,giftsPersonList);
        log.info("Hosp RelationPersonList: {}", relationPersonList.size());
        return relationPersonList;
    }
    @Override
    public List<GiftsRelationPersonEntity> saveOrUpdateGiftsPerson(Long applicationId,Date currentDate,String type,
                                                                   List<GiftsPersonEntity> giftsPersonList) {
        GiftsRelationPersonEntity person;
        List<GiftsRelationPersonEntity> list = Lists.newArrayList();
        for(GiftsPersonEntity giftPerson : giftsPersonList){
            person = new GiftsRelationPersonEntity();
            person.setApplicationId(applicationId);
            person.setPersionId(giftPerson.getId());
            person.setPositionTitle(giftPerson.getPositionTitle());
            person.setPersonName(giftPerson.getPersonName());
            person.setCompanyName(giftPerson.getCompanyName());
            person.setType(type);
            person.setCreatedDate(currentDate);
            person.setLastModifiedDate(currentDate);
            person.setVolume(giftPerson.getVolume());
            person.setMoney(giftPerson.getUnitValue());
            person.setMarkDeleted(Constant.NO_EXIST_MARK);
            giftsRelationPersonDao.insert(person);
            list.add(person);
        }
        return list;
    }

    @Override
    public List<GiftsRelationPersonEntity> saveOrUpdateGiftsPerson(List<GiftCompInfoForm> giftCompInfoFormList,
                                                                   Date currentDate, Long applicationId, Long userId,
                                                                   Long fileId, String type) {
        log.info("save giving gifts person...");
        log.info("before merge company form attachment person size: {}", giftCompInfoFormList.size());
        mergeGiftFromFileAttach(currentDate,applicationId,userId,fileId, giftCompInfoFormList);
        log.info("after merge company from attachment person size: {}", giftCompInfoFormList.size());

        if(CollectionUtils.isEmpty(giftCompInfoFormList)){
            return Collections.emptyList();
        }
        List<GiftsPersonEntity> giftsPersonList = saveGiftPerson(giftCompInfoFormList, userId);
        giftsRelationPersonDao.deleteByApplicationId(applicationId,type);
        List<GiftsRelationPersonEntity> relationPersonList = saveOrUpdateGiftsPerson(applicationId,currentDate,type,giftsPersonList);
        log.info("Gift RelationPersonList: {}", relationPersonList.size());
        return relationPersonList;
    }

    private void validatePersonCount(List<GiftCompInfoForm> giftCompInfoFormList,
                                     Integer volume) {
        Integer totalPersons = giftCompInfoFormList.stream().mapToInt(c -> c.getPersonList().size()).sum();
        if(!volume.equals(totalPersons)){
            throw new BaseException(400,Constant.GIFTS_VALIDATE_PERSON_COUNT_ERROR);
        }
    }

    private List<GiftsPersonEntity> saveGiftPerson(List<GiftCompInfoForm> giftCompInfoFormList, Long userId) {
//        validatePersonCount(giftCompInfoFormList,volume);
        List<GiftsCompanyEntity> giftsCompanyEntityList = saveGiftsCompany(giftCompInfoFormList,userId);
        Map<String, List<GiftsPersonEntity>> personMap = giftCompInfoFormList.stream().collect(Collectors.toMap(
                c -> StringUtils.trim(c.getCompanyName()), GiftCompInfoForm::getPersonList, (oldValue, newValue) -> newValue));
        for(GiftsCompanyEntity companyEntity: giftsCompanyEntityList){
            List<GiftsPersonEntity> persons = personMap.get(companyEntity.getCompanyName());
            saveOrUpdateGiftsPerson(companyEntity,persons,userId);
        }

        List<GiftsPersonEntity> giftsPersonList = giftsCompanyEntityList.stream().
                flatMap(g -> g.getPersonList().stream()).collect(Collectors.toList());
        log.info("after merge gift person size: {}", giftsPersonList.size());
        return giftsPersonList;
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


    private void mergeHospFromFileAttach(Date currentDate,Long applicationId,
                                          Long fileId,
                                         UserExtensionEntity user,
                                         List<GiftCompInfoForm> giftCompInfoFormList) {
        if(Objects.isNull(fileId)){
            log.info("fileId is empty...");
            return;
        }
        Long userId = user.getSfUserId();
        String companyCode = user.getCompanyCode();
        FileUploadEntity fileUpload = storageService.getById(fileId);
        if(Objects.nonNull(fileUpload) && StringUtils.isNotEmpty(fileUpload.getFilePath())){
            File file = new File(ManageConfig.UPLOAD_FILE_PATH + fileUpload.getFilePath());
            if(!file.exists()){
                return;
            }
            Map<String, GiftCompInfoForm> companyFromMap = giftCompInfoFormList.stream().collect(Collectors.toMap(
                    c -> StringUtils.trim(c.getCompanyName()), c -> c, (oldValue, newValue) -> newValue));
            Set<String> companyNames = companyFromMap.keySet();
            log.info("company names: {}", companyNames);
            try {
                EasyExcelUtil easyExcelUtil = new EasyExcelUtil();
                Pair<List<HospCompanyPersonModel>, Integer> pair =
                        easyExcelUtil.readExcel2007(
                                Files.newInputStream(file.toPath()), 1,1,
                                ExcelTypeEnum.XLSX, HospCompanyPersonModel.class);
                if(CollectionUtils.isNotEmpty(pair.getKey())){
                    List<HospCompanyPersonModel> hospModels = pair.getKey();
                    // userInfo.companyCode === '0813' || userInfo.companyCode === '2614' || userInfo.companyCode === '1391
                    if(Constant.GIFTS_LE_CODE_BCL_0813.equals(companyCode) ||
                            Constant.GIFTS_LE_CODE_BCS_2614.equals(companyCode) ||
                            Constant.GIFTS_LE_CODE_BCS_1391.equals(companyCode)) {
                        hospModels = hospModels.stream().filter(h -> !"HCP".equals(h.getIsGoSoc()))
                                .collect(Collectors.toList());
                    }

                    List<GiftsDictionaryEntity> isGoSocDictList =
                            Constant.GIFTS_DICT_MAP.get(Pair.of("isGoSoc", Constant.GIFTS_LANGUAGE_CN));
                    List<GiftsDictionaryEntity> isBayerCustDictList =
                            Constant.GIFTS_DICT_MAP.get(Pair.of("isBayerCustomer", Constant.GIFTS_LANGUAGE_CN));

                    Map<String, List<GiftsPersonEntity>> personMapFromAttach = hospModels.stream().map(m -> {
                        GiftsPersonEntity person = new GiftsPersonEntity();
                        BeanUtils.copyProperties(m,person);
                        String isGoSco = m.getIsGoSoc();
                        String isBayerCust = m.getIsBayerCustomer();
                        String isGoScoCode =  isGoSocDictList.stream().filter(g -> g.getName().equals(isGoSco))
                                .map(GiftsDictionaryEntity::getCode).findFirst().orElse(StringUtils.EMPTY);
                        String isBayerCustCode = isBayerCustDictList.stream().filter(g -> g.getName().equals(isBayerCust))
                                .map(GiftsDictionaryEntity::getCode).findFirst().orElse(StringUtils.EMPTY);
                        person.setIsGoSoc(isGoScoCode);
                        person.setIsBayerCustomer(isBayerCustCode);
                        log.info(">>>> isGoSco from excel: {}, isGoSco map code: {}", isGoSco,isGoScoCode);
                        log.info(">>>> isBayerCustomer from excel: {}, isBayerCustomer map code: {}", isBayerCust,isBayerCustCode);
                        return person;
                    }).collect(Collectors.groupingBy(g -> StringUtils.trim(g.getCompanyName()), Collectors.toList()));

                    for(Map.Entry<String, List<GiftsPersonEntity>> entry : personMapFromAttach.entrySet()){
                        String companyName = entry.getKey();
                        List<GiftsPersonEntity> personsFromAttach = entry.getValue();
                        log.info("persons from attachment size: {}", personsFromAttach.size());
                        if(companyNames.contains(companyName)){
                            log.info("exist in request company from...");
                            GiftCompInfoForm giftCompInfoForm = companyFromMap.get(companyName);
                            List<GiftsPersonEntity> personsFromRequest = giftCompInfoForm.getPersonList();
                            log.info("persons from request size: {}", personsFromRequest.size());
                            List<GiftsPersonEntity> afterMergePersons = Stream.of(personsFromAttach,personsFromRequest)
                                    .distinct().flatMap(Collection::stream).collect(Collectors.toList());
                            log.info("after merge person size: {}", afterMergePersons.size());
                            giftCompInfoForm.setPersonList(afterMergePersons);
                        }else {
                            log.info("not exist in request company from...");
                            GiftCompInfoForm giftCompInfoForm = new GiftCompInfoForm();
                            giftCompInfoForm.setCompanyName(companyName);
                            giftCompInfoForm.setPersonList(personsFromAttach);
                            giftCompInfoFormList.add(giftCompInfoForm);
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

    private void mergeGiftFromFileAttach(Date currentDate,Long applicationId,
                                     Long userId, Long fileId,
                                     List<GiftCompInfoForm> giftCompInfoFormList) {
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
            Map<String, GiftCompInfoForm> companyFromMap = giftCompInfoFormList.stream().collect(Collectors.toMap(
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
                            GiftCompInfoForm giftCompInfoForm = companyFromMap.get(companyName);
                            List<GiftsPersonEntity> personsFromRequest = giftCompInfoForm.getPersonList();
                            log.info("persons from request size: {}", personsFromRequest.size());
                            List<GiftsPersonEntity> afterMergePersons = Stream.of(personsFromAttach,personsFromRequest)
                                    .distinct().flatMap(Collection::stream).collect(Collectors.toList());
                            log.info("after merge person size: {}", afterMergePersons.size());
                            giftCompInfoForm.setPersonList(afterMergePersons);
                        }else {
                            log.info("not exist in request company from...");
                            GiftCompInfoForm giftCompInfoForm = new GiftCompInfoForm();
                            giftCompInfoForm.setCompanyName(companyName);
                            giftCompInfoForm.setPersonList(personsFromAttach);
                            giftCompInfoFormList.add(giftCompInfoForm);
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


    private List<GiftsCompanyEntity> saveGiftsCompany(List<GiftCompInfoForm> giftCompInfoFormList, Long userId) {
        List<String> companyNames = giftCompInfoFormList.stream().map(c -> StringUtils.trim(c.getCompanyName())).distinct()
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
            person.setIsBayerCustomer(fromRequestPerson.getIsBayerCustomer());
            person.setIsGoSoc(fromRequestPerson.getIsGoSoc());
            person.setUnitValue(fromRequestPerson.getUnitValue());
            person.setVolume(fromRequestPerson.getVolume());
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
            giftsPerson.setVolume(notMatchPerson.getVolume());
            giftsPerson.setUnitValue(notMatchPerson.getUnitValue());
            giftsPerson.setIsGoSoc(notMatchPerson.getIsGoSoc());
            giftsPerson.setIsBayerCustomer(notMatchPerson.getIsBayerCustomer());
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
