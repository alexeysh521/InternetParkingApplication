package com.example.RETURN.services;

import com.example.RETURN.dto.CarDto;
import com.example.RETURN.models.Car;
import com.example.RETURN.models.User;
import com.example.RETURN.repositories.CarRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class CarService {

    @Autowired private CarRepository carRepository;

    @Autowired private ModelMapper modelMapper;

    @Transactional
    public void save(Car car){
        carRepository.save(car);
    }

    public List<Car> findByUser(User user){
        return carRepository.findByUser(user).orElse(null);
    }

    public boolean existsByNumber(String number){
        return carRepository.existsByNumber(number);
    }

    @Transactional
    public CarDto forCreateCar(CarDto request, User user){
        Car car = new Car(
                request.getModel(),
                request.getNumber(),
                request.getColor(),
                user
        );
        carRepository.save(car);

        return convertToDto(car);
    }

    public CarDto convertToDto(Car car){
        return modelMapper.map(car, CarDto.class);
    }

}
