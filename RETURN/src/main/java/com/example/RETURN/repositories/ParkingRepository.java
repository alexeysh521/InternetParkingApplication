package com.example.RETURN.repositories;

import com.example.RETURN.enums.ParkingSlotNumber;
import com.example.RETURN.enums.ParkingSlotSize;
import com.example.RETURN.models.ParkingSpace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParkingRepository extends JpaRepository<ParkingSpace, Long> {

    boolean existsByParkingSlotNumber(ParkingSlotNumber number);

    List<ParkingSpace> findByParkingSlotSize(ParkingSlotSize parkingSlotSize);
}
