package com.example.RETURN.dto;

import com.example.RETURN.enums.ParkingSlotNumber;
import com.example.RETURN.enums.ParkingSlotSize;
import com.example.RETURN.models.Order;
import lombok.Data;

import java.util.List;

@Data
public class InfoParkingSpaceDto {
    private int id;

    private ParkingSlotNumber parkingSlotNumber;

    private ParkingSlotSize parkingSlotSize;

    private Boolean status;

    private List<Order> orders;
}
