package com.example.RETURN.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class TerminateOrderDto {

    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "Номер должен содержать только латинские буквы и цифры")
    private String number;

}
