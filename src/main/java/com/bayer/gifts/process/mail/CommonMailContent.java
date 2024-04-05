package com.bayer.gifts.process.mail;


import com.bayer.gifts.process.common.MailContentFieldIgnore;
import com.bayer.gifts.process.config.MailConfig;
import com.bayer.gifts.process.mail.vo.BaseMailVo;
import com.bayer.gifts.process.mail.vo.NoticeMailVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.File;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
public class CommonMailContent {

    private String mail_from = "e-grift";

    private String mail_sender;

    private String mailAttachmentUrl;

    private String status;

    private String executionId;
    private String taskId;

    private Long applicationId;

    private String mailTo;

    private String template;

    private VelocityContext context;

    private VelocityEngine ve;

    public CommonMailContent() {
        ve = new VelocityEngine();
        context = new VelocityContext();
        initVelocityEngine();
        initDoctorMailContent();
    }

    private void initDoctorMailContent() {
//        this.mail_sender = MailConfig.MAIL_SENDER;
        this.mail_from = "1";
    }

    private void initVelocityEngine() {
        try {
            ve.setProperty(VelocityEngine.FILE_RESOURCE_LOADER_PATH, MailConfig.MAIL_TEMPLATE_PATH);
            ve.setProperty("runtime.log.logsystem.class", "org.apache.velocity.runtime.log.NullLogSystem");
            ve.init();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Init velocity engine error: ", e);
        }
    }

    private String getCommonTemplate(String tpath, String template) {
        String body = "";
        try (StringWriter writer = new StringWriter()) {
            if (ve == null) {
                throw new Exception("Init velocity engine error!");
            }
            ve.mergeTemplate(tpath + File.separator + template, "utf-8", context, writer);
            body = writer.toString();
            return body;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return body;
    }

    public String getExecutionId() {
        return executionId;
    }

    public String getTaskId() {
        return taskId;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setExtraId(BaseMailVo baseMailVo) {
        if(baseMailVo instanceof NoticeMailVo){
           NoticeMailVo noticeMailVo = (NoticeMailVo) baseMailVo;
           this.executionId =  noticeMailVo.getExecutionId();
           this.taskId = noticeMailVo.getTaskId();
           this.applicationId = noticeMailVo.getApplicationId();
        }else {
            this.executionId =  StringUtils.EMPTY;
            this.taskId = StringUtils.EMPTY;
        }
    }

    public String getMailBody(BaseMailVo baseMailVo) {
        if(StringUtils.isNotEmpty(baseMailVo.getErrorLog())){
            return baseMailVo.getErrorLog();
        }
        setContextMap(baseMailVo);
        String tPath = StringUtils.EMPTY;
        /*if(baseMailVo instanceof JobMailVo){
            tPath = MailConfig.MAIL_TEMPLATE_JOB_PATH;
        }else*/ if(baseMailVo instanceof NoticeMailVo){
            tPath = MailConfig.MAIL_TEMPLATE_NOTICE_PATH;
        }else {
            //
        }
        return getCommonTemplate(tPath, this.getTemplate());
    }

    public void setContextMap(BaseMailVo baseMailVo) {
        if (null != baseMailVo) {
            Class clazz = baseMailVo.getClass();
            List<Field> fieldsList = new ArrayList<>();
            while (clazz != null) {
                Field[] declaredFields = clazz.getDeclaredFields();
                fieldsList.addAll(Arrays.asList(declaredFields));
                clazz = clazz.getSuperclass();
            }
            for (Field field : fieldsList) {
                field.setAccessible(true);
                MailContentFieldIgnore fieldIgnore =  field.getAnnotation(MailContentFieldIgnore.class);
                if(Objects.nonNull(fieldIgnore) && fieldIgnore.value()){
                    log.debug("Mail content ignore field >>> {}", field.getName());
                    continue;
                }
                try {
                    context.put(field.getName(), field.get(baseMailVo));
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
            context.put("newLine", "\n");
        }
    }

    public String getMail_cc() {
        return null;
    }

    public String getMail_from() {
        return mail_from;
    }

    public String getMail_sender() {
        return mail_sender;
    }

    public String getMailTo(String mailToStr) {
        return "";
    }

    public String getMailTo() {
        return mailTo;
    }

    public void setMailTo(String mailTo) {
        this.mailTo = mailTo;
    }

    public String getStatus() {
        return status;
    }

    public void setContext(VelocityContext context) {
        this.context = context;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getTemplate() {
        return template;
    }


    public void setMailAttachmentUrl(String mailAttachmentUrl) {
        this.mailAttachmentUrl = mailAttachmentUrl;
    }

    public String getMailAttachmentUrl() {
        return mailAttachmentUrl;
    }
}
