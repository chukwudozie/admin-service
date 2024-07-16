package com.stitch.admin.service;

public interface EmailService {
    boolean sendPasswordResetOTP(String mailBody, String email, String mailTitle);
}
