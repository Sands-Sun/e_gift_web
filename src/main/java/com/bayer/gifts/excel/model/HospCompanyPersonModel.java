package com.bayer.gifts.excel.model;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HospCompanyPersonModel extends BaseRowModel {
    @ExcelProperty(value = "Category", index = 0)
    private String isGoSoc;
    @ExcelProperty(value = " Is the company/entity a current Bayer customer", index = 1)
    private String isBayerCustomer;
    @ExcelProperty(value = "Company Name", index = 2)
    private String companyName;
    @ExcelProperty(value = "Person Name", index = 3)
    private String personName;
    @ExcelProperty(value = "Position Title", index = 4)
    private String positionTitle;
}
