package com.example.RETURN.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TerminateOrderDto {

    @NotNull(message = "Поле не должно быть пустым.")
    private long orderId;

}
