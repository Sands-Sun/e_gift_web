package com.bayer.gifts.process.variables;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class GivingGiftsApplyVariable implements Serializable {

    private static final long serialVersionUID = -4693004648737566736L;

    private Long applicationId;
    private Long applyForId;
    private String applyName;
    private String applyDate;
    private Long supervisorId;
    private String supervisorName;
    private Double unitValue;
    private Integer volume;
    private Double totalValue;
    private String giftDescType;
    private String giftsDescription;
    private String reasonType;
    private String referenceNo;
    private String signature;

    private String givingPersons;
    private String givingCompany;

    private String companyCode;

    //是否是政府官员或国有企业员工
    private String isGoSoc;
    //接受者是否是拜耳现有客户
    private String isBayerCustomer;

    List<Long> copyToUserIds;

//    private String SCOGroup;
//    private String DepartmentHeadGroup;
//    private String CountryHeadGroup;

    private List<Long> scoGroupUsers;
    private List<Long> departmentHeadGroupUsers;
    private List<Long> countryHeadGroupUsers;
}
