package com.example.RETURN.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDateTime;

public class OrderDto {

    @NotNull(message = "Размеры парковочного места обязательны")
    @Pattern(regexp = "s|m|l|xl|xxl|xxxl", message = "введен некорректный размер парковочного места")
    private String size;

    @NotNull(message = "Поле обязательно")
    @FutureOrPresent(message = "Введите корректную дату")
    private LocalDateTime startTime;

    private Boolean status_order = true;

    @NotNull(message = "Поле обязательно")
    @Future(message = "Введите корректную дату")
    private LocalDateTime endTime;

    /// ///

    public boolean isValidDuration(){
        return startTime != null && endTime != null && endTime.isAfter(startTime);
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public boolean isStatus_order() {
        return status_order;
    }

    public void setStatus_order(boolean status_order) {
        this.status_order = status_order;
    }
}
