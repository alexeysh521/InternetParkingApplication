package com.example.RETURN.services;

import com.example.RETURN.dto.AnEntityWithAnIdOnlyDto;
import com.example.RETURN.dto.CreateParkingSpaceDto;
import com.example.RETURN.dto.InfoParkingSpaceDto;
import com.example.RETURN.enums.ParkingSlotNumber;
import com.example.RETURN.enums.ParkingSlotSize;
import com.example.RETURN.models.ParkingSpace;
import com.example.RETURN.repositories.ParkingRepository;
import com.example.RETURN.services.impl.ParkingService;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ParkingServiceImpl implements ParkingService {

    private final ParkingRepository parkingRepository;

    private final ModelMapper modelMapper;

    public ParkingServiceImpl(ParkingRepository parkingRepository, ModelMapper modelMapper) {
        this.parkingRepository = parkingRepository;
        this.modelMapper = modelMapper;
    }

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
    public InfoParkingSpaceDto forCreateParkingSpace(CreateParkingSpaceDto parkingDto){
        ParkingSpace parking = new ParkingSpace(
                ParkingSlotNumber.fromStringNumber(parkingDto.getNumber()),
                ParkingSlotSize.fromStringParking(parkingDto.getSize())
        );
        parkingRepository.save(parking);
        return convertToDto(parking);
    }

    public List<ParkingSpace> findBySize(ParkingSlotSize size){
        List<ParkingSpace> spaces = parkingRepository.findByParkingSlotSize(size);
        if(spaces.isEmpty())
            throw new EntityNotFoundException("Парковочное место не найдено.");
        return spaces;
    }

    @Transactional
    public InfoParkingSpaceDto forDeleteParkingSpace(AnEntityWithAnIdOnlyDto dto){
        List<ParkingSpace> space = parkingRepository.findByIdList(dto.getId());

        return space.stream()
                .filter(ParkingSpace::isStatus)//если он true - то удалит, иначе исключение
                .peek(ps -> parkingRepository.delete(ps))
                .map(this::convertToDto)
                .findFirst()
                .orElseThrow(() ->
                        new EntityNotFoundException("Чтобы удалить парковочное место необходимо освободить его."));
    }

    public InfoParkingSpaceDto convertToDto(ParkingSpace space){
        return modelMapper.map(space, InfoParkingSpaceDto.class);
    }

}
