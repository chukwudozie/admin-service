package com.stitch.admin.utils;

import org.springframework.http.HttpStatus;

import java.time.format.DateTimeFormatter;

public final class Constants {

    private Constants(){}

    public static final String AUTH_URL= "api/v1/admin/auth";

    public static final String FAILED = "FAILED";
    public static  final String SUCCESS = "SUCCESS";

    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static HttpStatus status(int code){
        return (switch (code) {
            case 200 -> HttpStatus.OK;
            case 201 -> HttpStatus.CREATED;
            case 202 -> HttpStatus.ACCEPTED;
            case 400 -> HttpStatus.BAD_REQUEST;
            case 405 -> HttpStatus.CONFLICT;
            case 417 -> HttpStatus.EXPECTATION_FAILED;
            case 503, 504  -> HttpStatus.SERVICE_UNAVAILABLE;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        });
    }
}
