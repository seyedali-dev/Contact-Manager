package com.springboot.contactmanager.service;

import org.springframework.stereotype.Service;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

@Service
public class EmailService {
    public boolean sendEmail(String message, String subject, String to) {
        Properties properties = System.getProperties();
        properties.put("mail.smtp.host", "smtp.gmail.com");
        properties.put("mail.smtp.port", "465");
        properties.put("mail.smtp.ssl.enable", "true");
        properties.put("mail.smtp.auth", "true");

        //step1: get the session object
        Session session = Session.getInstance(properties, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication("learn.mike.helloworld@gmail.com", "tugoutumkwlgxrpl");
            }
        });
        session.setDebug(true);

        //step2: compose the message
        MimeMessage mimeMessage = new MimeMessage(session);
        try {
            String from = "learn.mike.helloworld@gmail.com";
            mimeMessage.setFrom(from);
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            mimeMessage.setSubject(subject);
//            mimeMessage.setText(message);
            mimeMessage.setContent(message, "text/html");

            //step3: send the message
            Transport.send(mimeMessage);
            System.out.println("--------\nmessage sent successfully!\n--------");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}
