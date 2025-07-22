package com.example.RETURN.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;


@Data
public class CarDto {

    private int id;

    @NotNull(message = "Поле не должно быть пустым.")
    private String model;

    @NotNull(message = "Поле не должно быть пустым.")
    @Pattern(regexp = "^[АВЕКМНОРСТУХ]{1}\\d{3}[АВЕКМНОРСТУХ]{2}RU\\d{2,3}$", message = "Некорректный ввод номера.")
    private String number;

    @NotNull(message = "Поле не должно быть пустым.")
    private String color;

    private String userName;

}
