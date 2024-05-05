package com.bayer.gifts.excel.model;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GiftsCompanyPersonModel extends BaseRowModel {

    @ExcelProperty(value = "Company Name", index = 0)
    private String companyName;
    @ExcelProperty(value = "Person Name", index = 1)
    private String personName;
    @ExcelProperty(value = "Position Title", index = 2)
    private String positionTitle;
}
