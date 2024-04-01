package com.bayer.gifts.process.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.bayer.gifts.process.dao.GiftsDropDownDao;
import com.bayer.gifts.process.entity.GriftDropDownEntity;
import com.bayer.gifts.process.service.GiftsDropDownService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Objects;

@Slf4j
@Service("mdDropDownService")
public class GiftsDropDownServiceImpl implements GiftsDropDownService {

    @Autowired
    GiftsDropDownDao GiftsDropDownDao;



    public String getReference() {
        String reference;
        GriftDropDownEntity dropDown =
                GiftsDropDownDao.selectOne(
                        Wrappers.<GriftDropDownEntity>lambdaQuery()
                                .eq(GriftDropDownEntity::getMarkDeleted, "N")
                                .eq(GriftDropDownEntity::getDropDownKey,"REFERENCE"));
        if (Objects.isNull(dropDown)) {
            reference = insertReference();
        } else{
            reference = dropDown.getDropDownName()
                    + dropDown.getDropDownTxt()
                    + dropDown.getDropDownValue();
            updateReference(dropDown);
        }
        return reference;
    }


    private String insertReference(){
        String reference;
        Calendar cal = new GregorianCalendar();
        Date date = cal.getTime();
        GriftDropDownEntity dropDown = new GriftDropDownEntity();
        dropDown.setCreatedDate(date);
        dropDown.setDropDownName("REF");
        dropDown.setDropDownTxt(cal.get(Calendar.YEAR) + "");
        dropDown.setDropDownValue("0000000");
        dropDown.setLastModifiedDate(date);
        dropDown.setDropDownKey("REFERENCE");
        dropDown.setMarkDeleted("N");
        // mdDropDown.setRemark("reference");
        reference = dropDown.getDropDownName()
                + dropDown.getDropDownTxt()
                + dropDown.getDropDownValue();
        updateReference(dropDown);
        GiftsDropDownDao.insert(dropDown);
        return reference;
    }

    private void updateReference(GriftDropDownEntity dropDown){
        String dropDownValue = GetDropDownValue(dropDown.getDropDownValue());
        String lastYear = dropDown.getDropDownTxt();
        Calendar cal = new GregorianCalendar();
        Date date = cal.getTime();
        dropDown.setDropDownTxt(cal.get(Calendar.YEAR) + "");
        dropDown.setLastModifiedDate(date);
        String currentYear = dropDown.getDropDownTxt();
        if (!lastYear.equals(currentYear)) {
            dropDown.setDropDownValue("0000000");
        } else {
            dropDown.setDropDownValue(dropDownValue);
        }
        GiftsDropDownDao.updateById(dropDown);
    }

    private String GetDropDownValue(String dropDownValue) {
        int dropdownValue = Integer.parseInt(dropDownValue);
        dropdownValue += 1;
        return ConvertNBitString(dropdownValue,7);
    }

    private String ConvertNBitString(Integer dropDownValue,int bit) {
        String DropDownValue = dropDownValue.toString();
        int length = DropDownValue.length();
        if (length < bit) {
            while (length < bit) {
                DropDownValue = "0" + DropDownValue;
                length = DropDownValue.length();
            }
        }
        return DropDownValue;
    }
}
