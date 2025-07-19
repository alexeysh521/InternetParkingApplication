package com.example.RETURN.enums;

import jakarta.persistence.EntityNotFoundException;

public enum ParkingSlotSize {
    S(2.3, 4.5, 2.0, 40),
    M(2.5, 5.0, 2.0, 60),
    L(2.7, 5.5, 2.2, 80),
    XL(3.0, 6.0, 2.5, 100),
    XXL(3.0, 10.0, 3.5, 150);

    private final double wight;
    private final double length;
    private final double width;
    private final int hourlyPrice;

    ParkingSlotSize(double wight, double width, double length, int price) {
        this.length = length;
        this.wight = wight;
        this.width = width;
        this.hourlyPrice = price;
    }

    public double getLength() {
        return length;
    }

    public double getWidth() {
        return width;
    }

    public double getWight() {
        return wight;
    }

    public int getHourlyPrice() {
        return hourlyPrice;
    }

    public static ParkingSlotSize fromStringParking(String str) {
        return ParkingSlotSize.valueOf(str.toUpperCase());
    }

    public static ParkingSlotSize fromIntPrice(int price){
        for(ParkingSlotSize size : ParkingSlotSize.values()){
            if(size.hourlyPrice == price){
                return size;
            }
        }
        throw new EntityNotFoundException("Неверная цена за парковочное место");
    }

    public static int getPriceByName(String size){
        try {
            return ParkingSlotSize.valueOf(size.toUpperCase()).hourlyPrice;
        }catch (EntityNotFoundException e){
            throw new EntityNotFoundException("Некорректный ввод");
        }
    }
}
