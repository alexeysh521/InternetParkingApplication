package com.example.RETURN.services;

import com.example.RETURN.dto.CarDto;
import com.example.RETURN.models.Car;
import com.example.RETURN.models.User;
import com.example.RETURN.repositories.CarRepository;
import com.example.RETURN.services.impl.CarService;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;

    private final ModelMapper modelMapper;

    public CarServiceImpl(CarRepository carRepository, ModelMapper modelMapper) {
        this.carRepository = carRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public void save(Car car){
        carRepository.save(car);
    }

    public List<CarDto> findByUser(int id){
        List<Car> cars = carRepository.findCarsByUserId(id);
        if(cars.isEmpty())
            throw new EntityNotFoundException("Автомобилей не найдено");
        return cars.stream()
                .map(this::convertToDto)
                .toList();
    }

    public List<CarDto> findAll(){
        return carRepository.findAll().stream()
                .map(this::convertToDto)
                .toList();
    }

    @Transactional
    public CarDto forCreateCar(CarDto request, User user){
        Car car = new Car(
                request.getModel(),
                request.getNumber(),
                request.getColor(),
                user
        );
        save(car);

        return convertToDto(car);
    }

    public CarDto convertToDto(Car car){
        return modelMapper.map(car, CarDto.class);
    }

}
