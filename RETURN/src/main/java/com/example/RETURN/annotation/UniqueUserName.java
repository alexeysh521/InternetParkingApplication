package com.example.RETURN.annotation;

import com.example.RETURN.valid.UniqueUserNameValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UniqueUserNameValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueUserName {
    String message() default "Этот логин уже занят";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
