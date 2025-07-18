package com.example.RETURN.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;

public class ExtendOrderDto {

    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "Номер должен содержать только латинские буквы и цифры")
    private String number;

    @Future
    @NotNull(message = "Поле обязательно")
    private LocalDateTime extendTime;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public LocalDateTime getExtendTime() {
        return extendTime;
    }

    public void setExtendTime(LocalDateTime extendTime) {
        this.extendTime = extendTime;
    }
}
