package com.stitch.admin.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = PasswordValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidPassword {

    String message () default "Valid password should contain at least one upper case," +
            " one lower case, special character and not less than 10 characters";

    Class<?> [] groups() default {};
    Class<? extends Payload> [] payload() default {};
}
