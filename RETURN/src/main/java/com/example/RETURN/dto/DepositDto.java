package com.example.RETURN.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class DepositDto {

    private Integer balance;

    @NotNull(message = "Неверное значение")
    @Pattern(regexp = "^(?i)(пополнить|снять|проверить)$", message = "Допустимы только значения пополнить|снять|проверить")
    private String nameOperation;

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    public String getNameOperation() {
        return nameOperation;
    }

    public void setNameOperation(String nameOperation) {
        this.nameOperation = nameOperation;
    }
}
