package com.bayer.gifts.process.utils;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class ExcelListener<T> extends AnalysisEventListener<T> {

    private List<T> datas = new ArrayList<>();

    private Integer lastRowNum = 0;

    @Override
    public void invoke(T object, AnalysisContext context) {
        try {
            lastRowNum = context.getCurrentRowNum();
            datas.add(object);
        }catch (IllegalArgumentException e) {
            log.error("parse error:",e);
        }
        doSomething(object);//根据自己业务做处理
    }

    private void doSomething(Object object) {
        //1、入库调用接口
        log.debug("read Object: {}",object);
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext analysisContext) {

    }

    public List<T> getDatas() {
        return datas;
    }

    public void setDatas(List<T> datas) {
        this.datas = datas;
    }

    public Integer getLastRowNum() {
        return lastRowNum;
    }

    public void setLastRowNum(Integer lastRowNum) {
        this.lastRowNum = lastRowNum;
    }
}
