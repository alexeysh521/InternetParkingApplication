package com.example.RETURN.enums;

import jakarta.persistence.EntityNotFoundException;

public enum DepositDtoSlotBalance {
    DEPOSIT, WITHDRAW, CHECK;

    public static DepositDtoSlotBalance getOperationFromString(String operation){
        try{
            return DepositDtoSlotBalance.valueOf(operation.trim().toUpperCase());
        }catch(IllegalArgumentException ex){
            throw new IllegalArgumentException(ex);
        }
    }

}
