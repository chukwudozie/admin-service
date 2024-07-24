package com.stitch.admin.exceptions.custom;

import lombok.Getter;

@Getter
public class ApiException extends RuntimeException {

    final int code;

    public ApiException(String message, int code) {
        super(message);
        this.code = code;
    }
}
