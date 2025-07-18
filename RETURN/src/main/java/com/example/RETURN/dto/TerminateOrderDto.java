package com.example.RETURN.dto;

import jakarta.validation.constraints.Pattern;

public class TerminateOrderDto {

    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "Номер должен содержать только латинские буквы и цифры")
    private String number;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
