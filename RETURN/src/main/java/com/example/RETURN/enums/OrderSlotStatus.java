package com.example.RETURN.enums;

import jakarta.persistence.EntityNotFoundException;

public enum OrderSlotStatus {
    ACTIVE, OVERDUE, COMPLETED;

    public static OrderSlotStatus getStatusFromString(String status){
        try{
            return OrderSlotStatus.valueOf(status.trim().toUpperCase());
        }catch (EntityNotFoundException ex){
            throw new EntityNotFoundException(ex);
        }
    }
}
