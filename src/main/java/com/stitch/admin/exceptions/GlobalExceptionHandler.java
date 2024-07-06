package com.stitch.admin.exceptions;


import com.stitch.admin.exceptions.custom.ApiException;
import com.stitch.admin.exceptions.custom.RegistrationException;
import com.stitch.admin.payload.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static com.stitch.admin.utils.Constants.FAILED;
import static com.stitch.admin.utils.Constants.status;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(RegistrationException.class)
    public ResponseEntity<ApiResponse<String>> handleRegistrationException(RegistrationException ex){
        ApiResponse<String> errorDetails = new ApiResponse<>(FAILED, HttpStatus.BAD_REQUEST.value(), ex.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiResponse<String>> handleApiExceptions(ApiException ex){
        ApiResponse<String> errorDetails = new ApiResponse<>(FAILED, ex.getCode(), ex.getMessage());
        return new ResponseEntity<>(errorDetails, status(ex.getCode()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String,String>>> handleInvalidMethodArgument(MethodArgumentNotValidException ex){
        Map<String,String> errors = new HashMap<>();
        log.error("Method arguments not valid ==> {}",ex.getMessage());
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName,errorMessage);
        });
        ApiResponse<Map<String,String>> errorDetails = new ApiResponse<>(FAILED, HttpStatus.BAD_REQUEST.value(), "Failed to validate request", errors);
        return new ResponseEntity<>(errorDetails, status(errorDetails.getCode()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Map<String,String>>> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("parameterName", ex.getName());
        errors.put("parameterValue", Objects.isNull(ex.getValue()) ? "" : String.valueOf(ex.getValue()));
        errors.put("message", ex.getMessage());
        ApiResponse<Map<String,String>> response = new ApiResponse<>(FAILED, HttpStatus.BAD_REQUEST.value(), "Argument Mismatch", errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Map<String,String>>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("Error Message", ex.getMessage());
        ApiResponse<Map<String,String>> response = new ApiResponse<>(FAILED, HttpStatus.BAD_REQUEST.value(), "Missing Or incomplete payload", errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotWritableException.class)
    public ResponseEntity<ApiResponse<Map<String,String>>> handleHttpMessageNotWritable(HttpMessageNotWritableException ex) {
        Map<String, String> errors = new HashMap<>();
        errors.put("Error Message", ex.getMessage());
        ApiResponse<Map<String,String>> response = new ApiResponse<>(FAILED, HttpStatus.BAD_REQUEST.value(), "Failed to Convert response", errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
