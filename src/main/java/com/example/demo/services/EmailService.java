package com.example.demo.services;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import com.example.demo.configs.ThymeleafConfig;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class EmailService {

    @Value("$(spring.mail.sender)")
     String sender;

    @Value("$(spring.mail.receiver)")
     String receiver;
    
    @Autowired
     JavaMailSender mailSender;

    @Autowired
    SpringTemplateEngine springTemplateEngine;

    public void sendEmail(String to, String subject) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        
        Context context = new Context();
        Map<String , Object> p = new HashMap();
        p.put("per", "abc");
        context.setVariables(p);
        String body = springTemplateEngine.process("confirm-email.html", context);
        message.setFrom(new InternetAddress(sender));
        message.setRecipients(MimeMessage.RecipientType.TO,"duong2lophot@gmail.com");
        message.setSubject(subject);
        message.setContent(body, "text/html; charset=utf-8");
        
        mailSender.send(message);
    }
}
