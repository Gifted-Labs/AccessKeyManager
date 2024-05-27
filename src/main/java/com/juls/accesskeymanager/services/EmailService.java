package com.juls.accesskeymanager.services;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.juls.accesskeymanager.data.models.EmailRequest;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {
    
    private final JavaMailSender mailSender;




    public void sendVerificationEmail(EmailRequest request){
        String subject = "EMAIL VERIFICATION";
        String message = "Please verify your account by clicking the following link : ";
    
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo("");
        mailMessage.setSubject(subject);
        mailMessage.setText(message);
        mailMessage.setFrom("juliusadjeteysowah@gmail.com");

        mailSender.send(mailMessage);
    }



    private class verificationMessage{
        private String subject = "EMAIL VERIFICATION";
        private String verificationEmailMessage;
        private String toEmail ;
         
    }

    public String getUrl(String verificationUrl){
        return String.format(
            "Thank you for signing up for MICRO FOCUS INC! We're excited to have you on board.\n\n" +
            "To complete your registration and activate your account, please verify your email address by clicking the link below:\n\n" +
            "<a href=\"%s\">VERIFICATION LINK</a><hr />\n" +
            "If the above link doesn't work, you can copy and paste the following URL into your browser:\n" +
            "%s\n\n" +
            "This link will expire in 15 minutes for security reasons. If you did not create an account using this email address, please ignore this email.\n" +
            "If you have any questions or need assistance, feel free to contact our support team.\n" +
            "Thank you,\n" +
            "The MICRO FOCUS Team", verificationUrl, verificationUrl);
    }

}
