package com.example.RETURN.valid;

import com.example.RETURN.annotation.UniqueNumberPS;
import com.example.RETURN.enums.ParkingSlotNumber;
import com.example.RETURN.repositories.ParkingRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class UniqueParkingValidator implements ConstraintValidator<UniqueNumberPS, String> {

    @Autowired private ParkingRepository parkingRepository;

    @Override
    public boolean isValid(String number, ConstraintValidatorContext context) {
        if(!StringUtils.hasText(number))
            return true;
        return !parkingRepository.existsByParkingSlotNumber(ParkingSlotNumber.fromStringNumber(number));
    }
}
