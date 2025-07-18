package com.example.RETURN.annotation;


import com.example.RETURN.valid.UniqueParkingValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;


@Documented
@Constraint(validatedBy = UniqueParkingValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueNumberPS {
    String message() default "Этот номер уже используется";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
