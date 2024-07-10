package com.stitch.admin.payload.response;

import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ApiResponse <T> {

    private String status;
    private int code;
    private String message;
    private T data;

    public ApiResponse(String status, int code, String message){
        this.status = status;
        this.code = code;
        this.message = message;
        this.data = (T) new HashMap<String,String>();
    }
}
