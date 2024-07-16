package com.stitch.admin.config.filter;

import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {


    @Bean
    public MailProperties mailProperties() {
        return new MailProperties();
    }
}
