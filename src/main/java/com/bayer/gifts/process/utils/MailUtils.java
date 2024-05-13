package com.bayer.gifts.process.utils;

import com.bayer.gifts.process.config.MailConfig;
import com.bayer.gifts.process.mail.entity.BatchCompleteMail;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import sun.misc.BASE64Encoder;

import javax.activation.DataHandler;
import javax.mail.*;
import javax.mail.internet.*;
import javax.mail.util.ByteArrayDataSource;
import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class MailUtils {



	private static Properties setSendProperty() {
		Properties sendProp = new Properties();
		sendProp.put("mail.debug", "true");
		sendProp.put("mail.host", MailConfig.MAIL_SMTP_HOST);
		sendProp.put("mail.transport.protocol", "smtp");
		sendProp.put("mail.smtp.auth", true);
		sendProp.put("mail.smtp.port", "25");
		System.setProperty("mail.mime.splitlongparameters","false");
		return sendProp;
	}

	private static void setRecipients(String mailTo,MimeMessage message, Message.RecipientType recipientType)
			throws MessagingException {
		if(!StringUtils.isEmpty(mailTo)){
			if (mailTo.contains(";")) {
				String[] split = mailTo.split(";");
				InternetAddress[] adr = new InternetAddress[split.length];
				for (int i = 0; i < split.length; i++) {
					if(!StringUtils.isEmpty(split[i]) )//防止配置时输入多个";" 导致分割错误
						adr[i] = new InternetAddress(split[i]);
				}
				message.setRecipients(recipientType, adr);
			} else {
				// 指明邮件的收件人
				message.setRecipient(recipientType, new InternetAddress(mailTo));
			}
		}
	}


	public static void sendMail(BatchCompleteMail completeMail) throws MessagingException, IOException {
		String content = completeMail.getMailBody();
		String attachment = completeMail.getMailAttachment();
		Properties sendProp = setSendProperty();
		// 1、创建session
		Session sendSession = Session.getInstance(sendProp);
		Transport ts = null;
		// 2、通过session得到transport对象
		ts = sendSession.getTransport();
		// 3、连上邮件服务器
		ts.connect(MailConfig.MAIL_SMTP_HOST, MailConfig.MAIL_SEND_USER, MailConfig.MAIL_SEND_PWD);
		// 4、创建邮件
		MimeMessage message = new MimeMessage(sendSession);
		//mailFile
		// 邮件消息头
		message.setFrom(new InternetAddress(MailConfig.MAIL_SEND_USER)); // 邮件的发件人

		// 邮件的收件人
		setRecipients(completeMail.getMailTo(), message,Message.RecipientType.TO);
		// 邮件CC
		setRecipients(completeMail.getMailCc(), message,Message.RecipientType.CC);
		// 邮件的标题
		message.setSubject(completeMail.getMailSubject());
		MimeMultipart mm = new MimeMultipart();
		MimeBodyPart text = new MimeBodyPart();
		content = setBody(message, content);
		text.setContent(content, "text/html;charset=UTF-8");
		mm.addBodyPart(text);
		mm.setSubType("related");

		if(!StringUtils.isEmpty(attachment)) {
			MimeBodyPart attachPart = new MimeBodyPart();
			File file = new File(attachment);
			attachPart.attachFile(file);
			BASE64Encoder enc = new BASE64Encoder();
			attachPart.setFileName("=?GBK?B?" + enc.encode(file.getName().getBytes("GBK")) + "?=");
			mm.addBodyPart(attachPart);
		}
		message.setContent(mm);
		message.saveChanges();
		// 5、发送邮件
		ts.sendMessage(message, message.getAllRecipients());
		ts.close();
	}

	/**
	 * 发送邮件
	 */
	public static void sendMail(String mailTo,String subject,String content, String filePath) throws MessagingException, IOException {
		Properties sendProp = setSendProperty();
		// 1、创建session
		Session sendSession = Session.getInstance(sendProp);
		Transport ts = null;
		// 2、通过session得到transport对象
		ts = sendSession.getTransport();
		// 3、连上邮件服务器
		ts.connect(MailConfig.MAIL_SMTP_HOST, MailConfig.MAIL_SEND_USER, MailConfig.MAIL_SEND_PWD);
		// 4、创建邮件
		MimeMessage message = new MimeMessage(sendSession);
		//mailFile
		// 邮件消息头
		message.setFrom(new InternetAddress(MailConfig.MAIL_SEND_USER)); // 邮件的发件人
		// 邮件的收件人
		setRecipients(mailTo,message,Message.RecipientType.TO);
		// 邮件的标题
		message.setSubject(subject);
		MimeMultipart mm = new MimeMultipart();
		MimeBodyPart text = new MimeBodyPart();
		content = setBody(message, content);
		text.setContent(content, "text/html;charset=UTF-8");
		mm.addBodyPart(text);
		mm.setSubType("related");
		
		if(!StringUtils.isEmpty(filePath)) {
			MimeBodyPart attachPart = new MimeBodyPart();
			File file = new File(filePath);
			attachPart.attachFile(file);
			BASE64Encoder enc = new BASE64Encoder();
			attachPart.setFileName("=?GBK?B?" + enc.encode(file.getName().getBytes("GBK")) + "?=");
			mm.addBodyPart(attachPart);
		}
		message.setContent(mm);
		message.saveChanges();
		// 5、发送邮件
		ts.sendMessage(message, message.getAllRecipients());
		ts.close();
	}


	private static String setBody(Message msg, String contentBody) throws MessagingException, IOException {
		String subject = msg.getSubject();
		StringBuffer sb = new StringBuffer();
		sb.append("<HTML>\n");
		sb.append("<HEAD>\n");
		sb.append("<TITLE>\n");
		sb.append(subject + "\n");
		sb.append("</TITLE>\n");
		sb.append("<meta http-equiv='Content-Type' content='text/html; charset=UTF-8'>");
		sb.append("</HEAD>\n");
		sb.append("<BODY style='font-family: Calibri;font-size: 16px;'>\n");
		sb.append(addHref(contentBody.replaceAll("\r", "<br>")));
		sb.append("</BODY>\n");
		sb.append("</HTML>\n");
		msg.setDataHandler(new DataHandler(new ByteArrayDataSource(sb.toString(), "text/html;charset=utf8")));
		msg.setContent(sb.toString(), "text/html;charset=utf8");
		return sb.toString();
	}

	public static String addHref(String title) {
		StringBuffer ret = new StringBuffer();
		String regEx = "(http[s]?:\\/\\/([\\w-]+\\.)+[\\w-]+(:){0,1}+([\\w-./?%&=]*)){1,}?";
		Pattern p = Pattern.compile(regEx, 2);
		Matcher m = p.matcher(title);
		int j = 0;
		for (int i = 0; m.find(); i++) {
			ret.append(title, j, m.start(i));
			ret.append("<a href='" + m.group(i) + "'>");
			ret.append(title, m.start(i), m.end(i));
			ret.append("</a>");
			j = m.end(i);
		}
		ret.append(title.substring(j));
		if (j == 0)
			return title;
		return ret.toString();
	}
}
