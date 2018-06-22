package com.xman.view.email;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;


public class MailUtil {

    public void sendMail(String content) throws MessagingException {
        Properties props = new Properties();
        //使用smtp代理，且使用网易163邮箱
        props.put("mail.smtp.host", "smtp.163.cn");
        props.put("mail.smtp.auth", "true");
        MyAuthenticator myauth = new MyAuthenticator("jun18320786746@163.com", "0000");//自己的授权码
        Session session = Session.getInstance(props, myauth);
        //打开调试开关
        session.setDebug(true);

        MimeMessage message = new MimeMessage(session);
        InternetAddress fromAddress = new InternetAddress("jun18320786746@163.com");
        message.setFrom(fromAddress);
        InternetAddress toAddress = new InternetAddress("sjun945@outlook.com");
        message.addRecipient(Message.RecipientType.TO, toAddress);
        message.setSubject("Android mail");
        message.setText(content);// 设置邮件内容
        //message.setFileName("邮件附件");
        message.saveChanges(); //存储信息

        Transport transport = null;
        transport = session.getTransport("smtp");
        transport.connect("smtp.163.com", "jun18320786746@163.com", "0000");//自己的授权码
        transport.sendMessage(message, message.getAllRecipients());

        transport.close();
    }

    class MyAuthenticator extends javax.mail.Authenticator {
        private String strUser;
        private String strPwd;

        public MyAuthenticator(String user, String password) {
            this.strUser = user;
            this.strPwd = password;
        }

        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(strUser, strPwd);
        }
    }
}