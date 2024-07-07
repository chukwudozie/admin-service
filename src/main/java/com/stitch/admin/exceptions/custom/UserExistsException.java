package com.stitch.admin.exceptions.custom;

public class UserExistsException extends RuntimeException{

    public UserExistsException(String message){
        super(message);
    }
}
