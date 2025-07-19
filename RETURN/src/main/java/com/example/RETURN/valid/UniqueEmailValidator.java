package com.example.RETURN.valid;

import com.example.RETURN.annotation.UniqueEmail;
import com.example.RETURN.repositories.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UniqueEmailValidator implements ConstraintValidator<UniqueEmail, String> {

    @Autowired private UserRepository userRepository;

    @Override
    public boolean isValid(String email, ConstraintValidatorContext context) {
        if(email == null || email.isEmpty())
            return true;

        return !userRepository.existsByEmail(email);
    }
}
