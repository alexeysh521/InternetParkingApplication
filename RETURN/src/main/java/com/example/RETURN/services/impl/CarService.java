package com.example.RETURN.services.impl;

import com.example.RETURN.dto.CarDto;
import com.example.RETURN.models.Car;
import com.example.RETURN.models.User;

import java.util.List;

public interface CarService {

    void save(Car car);

    List<CarDto> findByUser(int id);

    List<CarDto> findAll();

    CarDto forCreateCar(CarDto request, User user);

    CarDto convertToDto(Car car);

}
