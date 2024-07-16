package com.stitch.admin.service.impl;

import com.stitch.admin.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    public boolean sendEmail(String mailBody, String email, String mailTitle) {

        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(email);
            mail.setSentDate(new Date());
            mail.setSubject(mailTitle);
            mail.setText(mailBody);
            mailSender.send(mail);
            return true;
        }catch (Exception e){
            log.error("Exception occurred while sending mail ==> {}",e.getMessage());
            return false;
        }

    }
}
