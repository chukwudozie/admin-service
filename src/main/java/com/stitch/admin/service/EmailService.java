package com.stitch.admin.service;

public interface EmailService {
    boolean sendEmail(String mailBody, String email, String mailTitle);
}
