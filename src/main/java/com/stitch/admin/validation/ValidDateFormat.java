package com.stitch.admin.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.ReportAsSingleViolation;
import jakarta.validation.constraints.NotNull;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = DateFormatValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@NotNull
@ReportAsSingleViolation
public @interface ValidDateFormat {
    String message() default "Invalid date format. Use yyyy-MM-dd";

    Class<?> [] groups() default {};
    Class<? extends Payload> [] payload() default  {};
}
