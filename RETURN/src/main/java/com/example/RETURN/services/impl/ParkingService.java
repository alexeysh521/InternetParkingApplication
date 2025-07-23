package com.example.RETURN.services.impl;

import com.example.RETURN.dto.AnEntityWithAnIdOnlyDto;
import com.example.RETURN.dto.CreateParkingSpaceDto;
import com.example.RETURN.dto.InfoParkingSpaceDto;
import com.example.RETURN.enums.ParkingSlotSize;
import com.example.RETURN.models.ParkingSpace;

import java.util.List;

public interface ParkingService {

    void create(ParkingSpace parking);

    List<ParkingSpace> findAll();

    List<InfoParkingSpaceDto> forFreeParkSpace();

    InfoParkingSpaceDto forCreateParkingSpace(CreateParkingSpaceDto parkingDto);

    List<ParkingSpace> findBySize(ParkingSlotSize size);

    InfoParkingSpaceDto forDeleteParkingSpace(AnEntityWithAnIdOnlyDto dto);

    InfoParkingSpaceDto convertToDto(ParkingSpace space);

}
