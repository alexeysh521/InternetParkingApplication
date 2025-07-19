package com.example.RETURN.dto;

import jakarta.validation.constraints.NotNull;

public class AnEntityWithAnIdOnlyDto {
    @NotNull
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
