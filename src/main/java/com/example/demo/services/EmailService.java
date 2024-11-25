package com.example.demo.services;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

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

    @KafkaListener(topics = "confirm-acount-topic", groupId = "group1")
    public void sendEmail(String messages) throws MessagingException {
        System.out.println("message" + messages);
        String subject = "Token của bạn.";
        String[] arr = messages.split(",");
        // String to = arr[0].substring(arr[0].indexOf('=' + 1));
        // String subject = arr[1].substring(arr[1].indexOf('=' + 1));
        // String html = arr[2].substring(arr[2].indexOf('=' +1));
        String html = "send-mail-reset-password.html";
        Context context = new Context();
        Map<String, Object> field = new HashMap<>();
        field.put("field", "abc");
        context.setVariables(field);
        // Context context = (Context)arr[3].substring(arr[3].indexOf('=' +1));

        MimeMessage message = mailSender.createMimeMessage();

        String body = springTemplateEngine.process(html, context);
        message.setFrom(new InternetAddress(sender));
        message.setRecipients(MimeMessage.RecipientType.TO, messages);
        message.setSubject(subject);
        message.setContent(body, "text/html; charset=utf-8");

        mailSender.send(message);
    }
}
