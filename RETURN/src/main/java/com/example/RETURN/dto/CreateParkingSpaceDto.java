package com.example.RETURN.dto;

import com.example.RETURN.annotation.UniqueNumberPS;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CreateParkingSpaceDto {

    @NotNull(message = "номер парковки обязателен")
    @Pattern(regexp = "^[A-Z0-9]+$", message = "Номер должен содержать только латинские буквы и цифры")
    @UniqueNumberPS
    private String number;//номер парковки

    @NotNull(message = "размеры парковочного места обязательны")
    @Pattern(regexp = "s|m|l|xl|xxl", message = "Введен некорректный размер парковочного места")
    @Pattern(regexp = "[A-Za-z0-9]+$", message = "Размер должен содержать только латинские буквы и цифры")
    private String size;//размеры парковочного места


}
