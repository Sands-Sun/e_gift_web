package com.bayer.gifts.excel.model;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

@Getter
@Setter
public class GiftsCompanyPersonModel extends BaseRowModel {

    @ExcelProperty(value = "Company Name", index = 0)
    private String companyName;
    @ExcelProperty(value = "Person Name", index = 1)
    private String personName;
    @ExcelProperty(value = "Position Title", index = 2)
    private String positionTitle;
    @ExcelProperty(value = "Unit Value", index = 3)
    private Double unitValue;
    @ExcelProperty(value = "Quantity", index = 4)
    private Integer volume;


    public boolean notEmpty() {
        return StringUtils.isNotEmpty(companyName)
                && StringUtils.isNotEmpty(personName)
                && StringUtils.isNotEmpty(positionTitle)
                && Objects.nonNull(unitValue)
                && Objects.nonNull(volume);
    }
}
