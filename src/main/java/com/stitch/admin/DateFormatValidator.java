package com.stitch.admin;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

import static com.stitch.admin.utils.Constants.FORMATTER;

@Slf4j
public class DateFormatValidator implements ConstraintValidator<ValidDateFormat, LocalDate> {

    @Override
    public void initialize(ValidDateFormat constraintAnnotation) {
    }

    @Override
    public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Let @NotNull handle null values
        }
        try {
            value.format(FORMATTER);
            return true;
        } catch (Exception e) {
            log.error("exception validating date value {}",e.getMessage());
            return false;
        }
    }
}
