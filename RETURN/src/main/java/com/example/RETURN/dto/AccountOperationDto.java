package com.example.RETURN.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class AccountOperationDto {

    private Integer balance;

    private String userName;

    @NotNull(message = "Неверное значение")
    @Pattern(regexp = "^(CHECK|WITHDRAW|DEPOSIT)$", message = "Допустимы только значения CHECK, WITHDRAW, DEPOSIT")
    private String nameOperation;

    public AccountOperationDto(String nameOperation, Integer balance, String userName) {
        this.nameOperation = nameOperation;
        this.balance = balance;
        this.userName = userName;
    }
}
