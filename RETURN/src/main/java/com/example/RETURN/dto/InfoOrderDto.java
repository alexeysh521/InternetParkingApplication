package com.example.RETURN.dto;

import com.example.RETURN.enums.OrderSlotStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InfoOrderDto {

    //private InfoUserDto user;
    private int id;

    private int userId;

    private String userName;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private int price;

    private int fine;

    private OrderSlotStatus orderSlotStatus;

    private CreateParkingSpaceDto parking;

}
