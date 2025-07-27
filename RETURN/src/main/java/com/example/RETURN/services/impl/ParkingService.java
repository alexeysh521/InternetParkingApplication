package com.example.RETURN.services.impl;

import com.example.RETURN.dto.AnEntityWithAnIdOnlyDto;
import com.example.RETURN.dto.CreateParkingSpaceDto;
import com.example.RETURN.dto.InfoOrderRateDto;
import com.example.RETURN.dto.InfoParkingSpaceDto;
import com.example.RETURN.enums.ParkingSlotSize;
import com.example.RETURN.models.ParkingSpace;

import java.util.List;

public interface ParkingService {

    void create(ParkingSpace parking);

    List<InfoParkingSpaceDto> fromViewAllParkingSpaces();

    List<InfoParkingSpaceDto> forFreeParkSpace();

    List<InfoOrderRateDto> forParkingSpacesInformation();

    InfoParkingSpaceDto forCreateParkingSpace(CreateParkingSpaceDto parkingDto);

    List<ParkingSpace> findBySize(ParkingSlotSize size);

    InfoParkingSpaceDto forDeleteParkingSpace(AnEntityWithAnIdOnlyDto dto);

    InfoParkingSpaceDto convertToDto(ParkingSpace space);

}
