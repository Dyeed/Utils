package com.dyeed.email.utils;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.Map;

/**
 * 邮件发送工具
 * @author dyeed
 */
@Component
public class EmailUtil {
    
    /**
     * 发件人
     */
    @Value("${spring.mail.username}")
    private String from;
    
    @Autowired
    private JavaMailSender jms;
    
    
    /**
     * 发送简单邮件
     * @param sendTo  收件人
     * @param subject 标题
     * @param text    正文
     * @return result
     */
    public Result sendSimpleEmail(String sendTo, String subject, String text) {
        Result result = new Result();
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            // 接收地址
            message.setTo(sendTo);
            // 标题
            message.setSubject(subject);
            // 内容
            message.setText(text);
            jms.send(message);
            
            result.setRet("邮件发送成功");
            result.setStatusCode(Result.CODE_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            result.setRet("邮件发送失败");
            result.setStatusCode(Result.CODE_FAILURE);
            result.setDetail(e.getMessage());
        }
        return result;
    }
    
    /**
     * 发送HTML格式的邮件
     * @param sendTo  收件人
     * @param subject 标题
     * @param html    带HTML格式的内容
     * @return result
     */
    public Result sendHtmlEmail(String sendTo, String subject, String html) {
        Result result = new Result();
        MimeMessage message;
        try {
            message = jms.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(sendTo);
            helper.setSubject(subject);
            helper.setText(html, true);
            jms.send(message);
            
            result.setRet("邮件发送成功");
            result.setStatusCode(Result.CODE_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            result.setRet("邮件发送失败");
            result.setStatusCode(Result.CODE_FAILURE);
            result.setDetail(e.getMessage());
        }
        return result;
    }
    
    /**
     * 发送带附件的邮件
     * @param sendTo  收件人
     * @param subject 标题
     * @param text    内容
     * @param useHtml 是否是html格式
     * @param f       附件
     * @return result
     */
    public Result sendAttachmentsMail(String sendTo, String subject, String text, boolean useHtml, File f) {
        Result result = new Result();
        MimeMessage message;
        try {
            message = jms.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(sendTo);
            helper.setSubject(subject);
            helper.setText(text, useHtml);
            // 传入附件
            FileSystemResource file = new FileSystemResource(f);
            helper.addAttachment(file.getFile().getName(), file);
            jms.send(message);
            
            result.setRet("邮件发送成功");
            result.setStatusCode(Result.CODE_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            result.setRet("邮件发送失败");
            result.setStatusCode(Result.CODE_FAILURE);
            result.setDetail(e.getMessage());
        }
        return result;
    }
    
    /**
     * 发送带静态资源的邮件
     * @param sendTo  收件人
     * @param subject 标题
     * @param text    带HTML格式的内容
     * @param statics Map<"静态资源cid", "静态资源文件路径">
     * @return result
     */
    public Result sendInlineMail(String sendTo, String subject, String text, Map<String, String> statics) {
        Result result = new Result();
        MimeMessage message;
        try {
            message = jms.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(from);
            helper.setTo(sendTo);
            helper.setSubject(subject);
            helper.setText(text, true);
            //<img src='cid:img01'/> cid为资源唯一标识
            statics.forEach((key, value) -> {
                FileSystemResource file = new FileSystemResource(value);
                try {
                    helper.addInline(key, file);
                } catch (MessagingException e) {
                    e.printStackTrace();
                }
            });
            jms.send(message);
            
            result.setRet("邮件发送成功");
            result.setStatusCode(Result.CODE_SUCCESS);
        } catch (Exception e) {
            e.printStackTrace();
            result.setRet("邮件发送失败");
            result.setStatusCode(Result.CODE_FAILURE);
            result.setDetail(e.getMessage());
        }
        return result;
    }
    
    @Data
    public static class Result {
        public static final int CODE_SUCCESS = 200;
        public static final int CODE_FAILURE = 400;
        // 状态
        private int statusCode;
        // 返回信息
        private String ret;
        // 详情
        private String detail;
    }
}