package com.bayer.gifts.excel.model;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.metadata.BaseRowModel;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Getter
@Setter
public class HospCompanyPersonModel extends BaseRowModel {
    @ExcelProperty(value = "Category", index = 0)
    private String isGoSoc;
    @ExcelProperty(value = "Company Name", index = 1)
    private String companyName;
    @ExcelProperty(value = "Person Name", index = 2)
    private String personName;
    @ExcelProperty(value = "Position Title", index = 3)
    private String positionTitle;

}
