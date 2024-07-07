package com.stitch.admin.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.EmailValidator;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class EmailDomainValidator implements ConstraintValidator<ValidEmail,String> {

    private Set<String> allowedDomains;
    @Override
    public void initialize(ValidEmail constraintAnnotation) {
        allowedDomains = Arrays.stream(constraintAnnotation.allowedDomains())
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isValid(String email, ConstraintValidatorContext constraintValidatorContext) {
        if (email == null || email.isEmpty()){
            return false;
        }
        if(!EmailValidator.getInstance().isValid(email)){
            return false;
        }
        String domain = email.substring(email.indexOf('@') + 1).toLowerCase();
        if(!allowedDomains.contains(domain) && !allowedDomains.isEmpty()) {
            return false;
        }
        return isDomainValid(domain);
    }

    private boolean isDomainValid(String domain) {
        try {
            InetAddress [] addresses = InetAddress.getAllByName(domain);
            return addresses.length > 0;
        }catch (UnknownHostException e){
            log.error("Unknown host exception for domain : {}",e.getMessage());
            return false;
        }
    }
}
