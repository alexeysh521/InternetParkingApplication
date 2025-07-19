package com.example.RETURN.services;

import com.example.RETURN.dto.AnEntityWithAnIdOnlyDto;
import com.example.RETURN.dto.ParkingSpaceDto;
import com.example.RETURN.enums.ParkingSlotNumber;
import com.example.RETURN.enums.ParkingSlotSize;
import com.example.RETURN.models.ParkingSpace;
import com.example.RETURN.repositories.ParkingRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ParkingService {

    @Autowired private ParkingRepository parkingRepository;

    @Transactional
    public void create(ParkingSpace parking) {
        parkingRepository.save(parking);
    }

    public List<ParkingSpace> findAll(){
        return parkingRepository.findAll();
    }

    public String forFreeParkSpace(){
        List<ParkingSpace> spaces = findAll();
        StringBuilder listSpaces = new StringBuilder();
        boolean flag = false;

        for(ParkingSpace ps : spaces){
            ParkingSlotSize size = ParkingSlotSize.fromStringParking(ps.getParkingSlotSize().name());
            if(ps.isStatus()) {
                flag = true;
                listSpaces.append(String.format("Свободно место %s, размер %s.\n",
                        ps.getParkingSlotNumber().name(), size));
            }
        }

        if(!flag && listSpaces.length() == 0)
            return "Свободных мест не осталось.";

        return listSpaces.toString().trim();
    }

    @Transactional
    public String forCreateParkingSpace(ParkingSpaceDto parkingDto){
        if(ParkingSlotNumber.isValidNumber(parkingDto.getNumber()))
            return "Неверный ввод парковочного места.";

        ParkingSlotSize size = ParkingSlotSize.fromStringParking(parkingDto.getSize().toUpperCase());
        ParkingSlotNumber number = ParkingSlotNumber.fromStringNumber(parkingDto.getNumber().toUpperCase());
        ParkingSpace parking = new ParkingSpace(
                number,
                size
        );
        parkingRepository.save(parking);
        return String.format("Создано парковочное место:\nномер %s \nразмер %s",
                parkingDto.getNumber(), parkingDto.getSize());
    }

    public List<ParkingSpace> findBySize(ParkingSlotSize size){
        List<ParkingSpace> spaces = parkingRepository.findByParkingSlotSize(size);
        if(spaces.isEmpty())
            throw new EntityNotFoundException("Парковочное место не найдено.");
        return spaces;
    }

    @Transactional
    public String forDeleteParkingSpace(AnEntityWithAnIdOnlyDto dto){
        ParkingSpace space = parkingRepository.findById((long) dto.getId()).orElseThrow(EntityNotFoundException::new);//есть ли вообще такое парк.место?
        if(!space.isStatus())
            throw new EntityNotFoundException("Что бы удалить парковочное место, освободите его.");
        parkingRepository.delete(space);
        return String.format("Парковочное место номер %s удалено", space.getParkingSlotNumber());
    }

}
