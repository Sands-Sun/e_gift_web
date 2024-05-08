package com.bayer.gifts.process.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@ConfigurationProperties(prefix = "mail")
@Component("mailConfig")
public class MailConfig {

    public static Boolean MAIL_DEBUG_MAIL_SEND;
    public static String MAIL_TEMPLATE_PATH;
    public static String MAIL_TEMPLATE_JOB_PATH;
    public static String MAIL_TEMPLATE_NOTICE_PATH;
    public static String MAIL_SEND_NOTIFY_TO;
    public static Boolean MAIL_AUTO_SEND;
    public static String MAIL_SMTP_HOST;
    public static String MAIL_SEND_USER;
    public static String MAIL_SEND_PWD;
    public static int MAIL_PORT;

    public MailConfig() {
    }

    public void setDebugMailSend(Boolean debugMailSend) {
        MAIL_DEBUG_MAIL_SEND = debugMailSend;
    }

    public void setTemplatePath(String templatePath) {
        MAIL_TEMPLATE_PATH = templatePath;
    }

    public void setNoticeTo(String noticeTo) {
        MAIL_SEND_NOTIFY_TO = noticeTo;
    }

    public void setTemplateJobPath(String templateJobPath) {
        MAIL_TEMPLATE_JOB_PATH = templateJobPath;
    }

    public void setTemplateNoticePath(String templateNoticePath) {
        MAIL_TEMPLATE_NOTICE_PATH = templateNoticePath;
    }

    public void setAutoSend(Boolean autoSend) {
        MAIL_AUTO_SEND = autoSend;
    }

    public void setSmtpHost(String smtpHost) {
       MAIL_SMTP_HOST = smtpHost;
    }

    public void setSendUser(String sendUser) {
        MAIL_SEND_USER = sendUser;
    }

    public void setSendPassword(String sendPassword) {
        MAIL_SEND_PWD = sendPassword;
    }

    public void setMailPort(int mailPort) {
        MAIL_PORT = mailPort;
    }
}
