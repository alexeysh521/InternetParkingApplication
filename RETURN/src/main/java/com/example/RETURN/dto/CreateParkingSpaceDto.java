package com.example.RETURN.dto;

import com.example.RETURN.annotation.UniqueNumberPS;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CreateParkingSpaceDto {

    @NotNull(message = "Номер парковки обязателен")
    @Pattern(regexp = "^[A-E](1[0-2]|[1-9])$", message = "Номер должен содержать только латинские буквы и цифры")
    @UniqueNumberPS
    private String number;//номер парковки

    @NotNull(message = "Размеры парковочного места обязательны")
    @Pattern(regexp = "(?i)s|m|l|xl|xxl", message = "Введен некорректный размер парковочного места")
    private String size;//размеры парковочного места


}
