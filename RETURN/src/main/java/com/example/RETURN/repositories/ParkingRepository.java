package com.example.RETURN.repositories;

import com.example.RETURN.enums.ParkingSlotNumber;
import com.example.RETURN.enums.ParkingSlotSize;
import com.example.RETURN.models.ParkingSpace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ParkingRepository extends JpaRepository<ParkingSpace, Integer> {

    boolean existsByParkingSlotNumber(ParkingSlotNumber number);

    List<ParkingSpace> findByParkingSlotSize(ParkingSlotSize parkingSlotSize);

    @Query("SELECT p FROM ParkingSpace p WHERE p.id = :id")
    List<ParkingSpace> findByIdList(@Param("id") int id);
}
