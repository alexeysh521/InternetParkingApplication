package com.example.RETURN.annotation;


import com.example.RETURN.valid.UniqueEmailValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UniqueEmailValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueEmail {
    String message() default "Этот email уже используется";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
