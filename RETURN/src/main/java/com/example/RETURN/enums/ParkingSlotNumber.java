package com.example.RETURN.enums;

public enum ParkingSlotNumber {
    A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12,
    B1, B2, B3, B4, B5, B6, B7, B8, B9, B10, B11, B12,
    C1, C2, C3, C4, C5, C6, C7, C8, C9, C10, C11, C12,
    D1, D2, D3, D4, D5, D6, D7, D8, D9, D10, D11, D12,
    E1, E2, E3, E4, E5, E6, E7, E8, E9, E10, E11, E12;

    public static ParkingSlotNumber fromStringNumber(String number){
        return ParkingSlotNumber.valueOf(number.toUpperCase());
    }

    public static boolean isValidNumber(String number){
        if(number == null || number.isBlank()) return false;

        try{
            ParkingSlotNumber.valueOf(number.trim().toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
