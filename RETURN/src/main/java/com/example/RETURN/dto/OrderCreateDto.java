package com.example.RETURN.dto;

import com.example.RETURN.enums.OrderSlotStatus;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderCreateDto {

    @NotNull(message = "Размеры парковочного места обязательны")
    @Pattern(regexp = "s|m|l|xl|xxl|xxxl", message = "введен некорректный размер парковочного места")
    private String size;

    @NotNull(message = "Поле обязательно")
    @FutureOrPresent(message = "Введите корректную дату")
    private LocalDateTime startTime;

    private OrderSlotStatus orderSlotStatus = OrderSlotStatus.ACTIVE;

    @NotNull(message = "Поле обязательно")
    @Future(message = "Введите корректную дату")
    private LocalDateTime endTime;

}
