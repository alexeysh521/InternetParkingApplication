package com.example.RETURN.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AnEntityWithAnIdOnlyDto {

    @NotNull
    private int id;

}
