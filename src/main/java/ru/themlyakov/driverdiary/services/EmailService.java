package ru.themlyakov.driverdiary.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Component
public class EmailService{
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private Environment environment;

    @Value(value = "${spring.mail.username}")
    private String username;

    public void sendMessage(String to,String subject,String text) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
        String htmlMsg = "Ваш код восстановления:\n"+text;
//mimeMessage.setContent(htmlMsg, "text/html"); /** Use this or below line **/
        helper.setText(htmlMsg, false); // Use this or above line.
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setFrom(username);
        mailSender.send(mimeMessage);
    }
}
