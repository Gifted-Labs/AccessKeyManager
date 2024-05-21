package com.juls.accesskeymanager.services;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {
    
    private final JavaMailSender mailSender;

    public void sendVerificationEmail(String to, String verificationTokenUrl){
        String subject = "EMAIL VERIFICATION";
        String  verificationUrl = verificationTokenUrl;
        String message = "Please verify your account by clicking the following link : "+verificationUrl;
    
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(to);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);
        mailMessage.setFrom("juliusadjeteysowah@gmail.com");

        mailSender.send(mailMessage);
    }

}
