package com.example.RETURN.valid;

import com.example.RETURN.annotation.UniqueUserName;
import com.example.RETURN.repositories.UserRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UniqueUserNameValidator implements ConstraintValidator<UniqueUserName, String> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean isValid(String userName, ConstraintValidatorContext context) {
        if(userName == null || userName.isEmpty())
            return true;

        return !userRepository.existsByUserName(userName);
    }
}
