package com.example.RETURN.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ExtendOrderDto {

    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "Номер должен содержать только латинские буквы и цифры")
    private String number;

    @Future
    @NotNull(message = "Поле обязательно")
    private LocalDateTime extendTime;

}
