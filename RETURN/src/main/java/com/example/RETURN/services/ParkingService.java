package com.example.RETURN.services;

import com.example.RETURN.dto.AnEntityWithAnIdOnlyDto;
import com.example.RETURN.dto.CreateParkingSpaceDto;
import com.example.RETURN.dto.InfoParkingSpaceDto;
import com.example.RETURN.enums.ParkingSlotNumber;
import com.example.RETURN.enums.ParkingSlotSize;
import com.example.RETURN.models.ParkingSpace;
import com.example.RETURN.repositories.ParkingRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ParkingService {

    @Autowired private ParkingRepository parkingRepository;

    @Autowired private ModelMapper modelMapper;

    @Transactional
    public void create(ParkingSpace parking) {
        parkingRepository.save(parking);
    }

    public List<ParkingSpace> findAll(){
        return parkingRepository.findAll();
    }

    public List<InfoParkingSpaceDto> forFreeParkSpace(){
        List<ParkingSpace> freeSpaces = findAll();

        return freeSpaces.stream()
                .filter(ParkingSpace::isStatus)
                .map(this::convertToDto)
                .toList();
    }

    @Transactional
    public String forCreateParkingSpace(CreateParkingSpaceDto parkingDto){
        if(!ParkingSlotNumber.isValidNumber(parkingDto.getNumber()))
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
        ParkingSpace space = parkingRepository.findById(dto.getId()).orElseThrow(EntityNotFoundException::new);//есть ли вообще такое парк.место?
        if(!space.isStatus())
            throw new EntityNotFoundException("Что бы удалить парковочное место, освободите его.");
        parkingRepository.delete(space);
        return String.format("Парковочное место номер %s удалено", space.getParkingSlotNumber());
    }

    public InfoParkingSpaceDto convertToDto(ParkingSpace space){
        return modelMapper.map(space, InfoParkingSpaceDto.class);
    }

}
