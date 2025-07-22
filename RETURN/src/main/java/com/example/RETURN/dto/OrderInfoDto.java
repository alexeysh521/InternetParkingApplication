package com.example.RETURN.dto;

import com.example.RETURN.enums.OrderSlotStatus;
import com.example.RETURN.enums.ParkingSlotNumber;
import com.example.RETURN.enums.ParkingSlotSize;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderInfoDto {

    //private UserInfoDto user;
    private int id;

    private String userName;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private int price;

    private OrderSlotStatus orderSlotStatus;

    private CreateParkingSpaceDto parking;

}
