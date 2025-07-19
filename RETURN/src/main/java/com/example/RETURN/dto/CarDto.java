package com.example.RETURN.dto;

import jakarta.validation.constraints.NotNull;

public class CarDto {
    @NotNull(message = "Поле не должно быть пустым.")
    private String model;

    @NotNull(message = "Поле не должно быть пустым.")
    private String number;

    @NotNull(message = "Поле не должно быть пустым.")
    private String color;

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

}
