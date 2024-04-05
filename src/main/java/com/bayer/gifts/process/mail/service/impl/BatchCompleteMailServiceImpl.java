package com.bayer.gifts.process.mail.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.bayer.gifts.process.common.Constant;
import com.bayer.gifts.process.mail.CommonMailContent;
import com.bayer.gifts.process.mail.dao.BatchCompleteMailDao;
import com.bayer.gifts.process.mail.entity.BatchCompleteMail;
import com.bayer.gifts.process.mail.entity.MailPolicy;
import com.bayer.gifts.process.mail.entity.MailTemplate;
import com.bayer.gifts.process.mail.service.BatchCompleteMailService;
import com.bayer.gifts.process.mail.vo.BaseMailVo;
import com.bayer.gifts.process.utils.MailUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

import static com.bayer.gifts.process.common.Constant.MAIL_TEMPLATE_MAP;


@Slf4j
@Service("batchCompleteMailService")
public class BatchCompleteMailServiceImpl extends ServiceImpl<BatchCompleteMailDao, BatchCompleteMail> implements BatchCompleteMailService {

    private CommonMailContent mailContent;
    private String mail_from;
    private boolean autoSent;
    private String mail_to = "";
    private String mail_cc = "";
    private String mail_subject;
    private String mail_body;
    private String mail_sender;
    private String status;

    private final int SYSTEM_USER = 1;

    @Override
    public void init() {
        log.info("Init mail content...");
        mailContent = new CommonMailContent();
    }

    @Override
    public BatchCompleteMail saveCompleteMail(BaseMailVo mailVo) {
        String mailType = mailVo.getMailType();
        String processType = mailVo.getProcessType();
        this.autoSent = mailVo.getAutoSent();
        log.info("Send processType >>>>>>{}, mailType >>>>> {}, autoSent >>>> {}",processType, mailType,autoSent);
        if(!MAIL_TEMPLATE_MAP.containsKey(processType)){
            log.info("Policy not contains processType: {}", processType);
            return null;
        }
        if(!MAIL_TEMPLATE_MAP.get(processType).containsKey(mailType)){
            log.info("Policy not contains mailType: {}", mailType);
            return null;
        }

        MailTemplate mailTemplate = MAIL_TEMPLATE_MAP.get(processType).get(mailType);
        MailPolicy mailPolicy = mailTemplate.getPolicyList().stream().filter(p -> p.getActionType()
                .equals(mailVo.getActionType())).findFirst().orElse(null);
        String policySubject = Objects.isNull(mailPolicy) ?  StringUtils.EMPTY : mailPolicy.getSubject();
        log.info("Policy Subject: {}", policySubject);
        if(StringUtils.isNotEmpty(mailVo.getSubjectContent())){
            this.mail_subject = String.format(mailVo.getSubjectContent(),policySubject);
        }else {
            this.mail_subject = policySubject;
        }
        mailContent.setTemplate(mailTemplate.getTemplate());
        mailContent.setMailTo(mailVo.getMailTo());
        mailContent.setExtraId(mailVo);
        this.mail_from = mailContent.getMail_from();
        this.mail_sender = mailVo.getMailSender();
        this.status = mailContent.getStatus();
//            this.mailToForRole = commonMailPolicy.getMailTo();
        this.mail_body = mailContent.getMailBody(mailVo);
        if(StringUtils.isNotEmpty(mailVo.getAttachment())){
            mailContent.setMailAttachmentUrl(mailVo.getAttachment());
        }
        return saveToCompleteMail(Constant.NO_EXIST_MARK,mail_cc,mailVo.getAttachment());
    }


    public BatchCompleteMail saveToCompleteMail() {
        String mail_bcc = "";
        String mail_attach = "";
        BatchCompleteMail completeMail = null;
        try {
            this.mail_to = mailContent.getMailTo();
            if (!StringUtils.isEmpty(mailContent.getMailAttachmentUrl())) {
                mail_attach = mailContent.getMailAttachmentUrl();
            }

            if (StringUtils.isEmpty(mail_to)) {
                return completeMail;
            }

            String mail_cc_user = getMaill_ccByType(this.SYSTEM_USER);
            if (this.mail_body != null) {
                log.info("mail_cc_user:" + mail_cc_user);
                completeMail = saveToCompleteMail(Constant.NO_EXIST_MARK,mail_bcc, mail_attach);

                log.info("\r\n" + "mail_from - " + mail_from + "\r\n"
                        + "mail_to - " + mail_to + "\r\n" + "mail_subject - "
                        + mail_subject + "\r\n" + "mail_body - " + mail_body
                        + "\r\n" + "mail_cc -ss- copyto " + mail_cc
                        + "\r\n" + "mail_bcc - " + mail_bcc + "\r\n"
                        + "mail_attach - " + mail_attach + "\r\n"
                        + "mail_sender - " + mail_sender + "\r\n");

            }

        } catch (Exception e) {
            log.error("send mail error: ",e);
        }
        return  completeMail;
    }

    public String getMaill_ccByType(int type) throws Exception {
        String[] mail_ccArray = Arrays.stream(mail_cc.split(",")).toArray(String[]::new);
        String systemUser_mailcc = "";
        StringBuilder notesUser_mailcc = new StringBuilder();

        for (String mail_cc_local : mail_ccArray) {
            notesUser_mailcc.append(",").append(mail_cc_local);
        }
        try {
            if (notesUser_mailcc.length() > 0)
                notesUser_mailcc = new StringBuilder(notesUser_mailcc.substring(1,
                        notesUser_mailcc.length()));
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("substring Exception"
                    + e.getMessage());
        }
        if (type == this.SYSTEM_USER)
            return systemUser_mailcc;
        else
            return notesUser_mailcc.toString();
    }

    private boolean sendMailWithStatus(String mailbcc, String mailAttch) throws MessagingException, IOException {
        boolean sendResult = false;
        if(autoSent){
            log.info("auto send mail...");
            saveToCompleteMail(Constant.EXIST_MARK,mailbcc, mailAttch);
            MailUtils.sendMail(mail_to,mail_subject,mail_body,mailAttch);
        }else {
            log.info("mail save to complete...");
            saveToCompleteMail(Constant.NO_EXIST_MARK,mailbcc, mailAttch);
        }
        log.info("mail From:{},mail subject:{},mail to:{},mail cc:{}", mail_from, mail_subject, mail_to, mail_cc);
        return sendResult;
    }


    private BatchCompleteMail saveToCompleteMail(String isSent,String mailBcc, String mailAttach) {
        BatchCompleteMail mail = new BatchCompleteMail();
        mail.setMailSender(mail_sender);
        mail.setMailFrom(mail_from);
        mail.setMailTo(mailContent.getMailTo());
        mail.setMailSubject(mail_subject);
        mail.setMailBody(mail_body);
        mail.setMailCc(mail_cc);
        mail.setMailBcc(mailBcc);
        mail.setMailAttachment(mailAttach);
        mail.setCreatedDate(new Date());
        mail.setIsSent(isSent);
        mail.setTaskId(mailContent.getTaskId());
        mail.setExecutionId(mailContent.getExecutionId());
        mail.setApplicationId(mailContent.getApplicationId());
        mail.setWrongTimes(0);
        baseMapper.insert(mail);
        return mail;
    }


}
