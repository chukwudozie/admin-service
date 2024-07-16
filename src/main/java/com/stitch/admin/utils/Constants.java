package com.stitch.admin.utils;

import org.springframework.http.HttpStatus;

import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

public final class Constants {

    private Constants(){}


    public static final String ADMIN_BASE_URL= "api/v1/admin";
    public static final String AUTH_URL= "api/v1/admin/auth";

    public static final String FAILED = "FAILED";
    public static  final String SUCCESS = "SUCCESS";

    public static final String PASSWORD_RESET_LINK = "http://localhost:8080/reset-passord";

    public static final String PASSWORD_RESET_TITLE = "Password Reset";

    public static final String RESET_PWD_OTP_EMAIL = "Your OTP to reset your password for STITCH is [OTP], click on this link" +
            "[LINK] and use your OTP to change your password. OTP expires after 3 minutes";

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static final Set<String> ALLOWED_URLS;

    static {
        ALLOWED_URLS = new HashSet<>();
        ALLOWED_URLS.add("/api/v1/admin/send-password-notification");
        ALLOWED_URLS.add("/api/v1/admin/auth/login");
    }

    public static HttpStatus status(int code){
        return (switch (code) {
            case 200 -> HttpStatus.OK;
            case 201 -> HttpStatus.CREATED;
            case 202 -> HttpStatus.ACCEPTED;
            case 400 -> HttpStatus.BAD_REQUEST;
            case 401 -> HttpStatus.UNAUTHORIZED;
            case 405 -> HttpStatus.CONFLICT;
            case 417 -> HttpStatus.EXPECTATION_FAILED;
            case 503, 504  -> HttpStatus.SERVICE_UNAVAILABLE;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        });
    }
}
