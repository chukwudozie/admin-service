package com.stitch.admin.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
@Slf4j
public final class Constants {

    private Constants(){}


    public static final String ADMIN_BASE_URL= "api/v1/admin";
    public static final String DASHBOARD_URL =  "api/v1/admin/dashboard";
    public static final String AUTH_URL= "api/v1/admin/auth";

    public static final String FAILED = "FAILED";
    public static  final String SUCCESS = "SUCCESS";

    public static final String PASSWORD_RESET_LINK = "http://localhost:8080/reset-passord";

    public static final String PASSWORD_RESET_TITLE = "Password Reset";

    public static final String RESET_PWD_OTP_EMAIL = "Your OTP to reset your password for STITCH is [OTP], click on this link" +
            "[LINK] and use your OTP to change your password. OTP expires after 3 minutes";

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final String EMPTY = "";

    public static final Set<String> ALLOWED_URLS;

    static {
        ALLOWED_URLS = new HashSet<>();
        ALLOWED_URLS.add("/api/v1/admin/send-password-notification");
        ALLOWED_URLS.add("/api/v1/admin/auth/login");
        ALLOWED_URLS.add("/api/v1/admin/reset-password");
    }

    public static HttpStatus status(int code){
        return (switch (code) {
            case 200 -> HttpStatus.OK;
            case 201 -> HttpStatus.CREATED;
            case 202 -> HttpStatus.ACCEPTED;
            case 400 -> HttpStatus.BAD_REQUEST;
            case 401 -> HttpStatus.UNAUTHORIZED;
            case 405 -> HttpStatus.CONFLICT;
            case 406 -> HttpStatus.NOT_ACCEPTABLE;
            case 417 -> HttpStatus.EXPECTATION_FAILED;
            case 503, 504  -> HttpStatus.SERVICE_UNAVAILABLE;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        });
    }

    public static Optional<String> getLoggedInUser() {
        try {
            SecurityContext context = SecurityContextHolder.getContext();
            if(Objects.nonNull(context)){
                Authentication authentication= context.getAuthentication();
                return Optional.ofNullable(authentication.getName());
            }
            return Optional.empty();
        }catch (Exception e){
            log.error("Exception occurred while fetching logged in user ==> {}",e.getMessage());
            return Optional.empty();
        }
    }
}
