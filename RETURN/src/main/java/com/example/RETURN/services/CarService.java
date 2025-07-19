package com.example.RETURN.services;

import com.example.RETURN.dto.CarDto;
import com.example.RETURN.models.Car;
import com.example.RETURN.models.User;
import com.example.RETURN.repositories.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CarService {

    @Autowired private CarRepository carRepository;

    @Transactional
    public void save(Car car){
        carRepository.save(car);
    }

    public boolean existsByNumber(String number){
        return carRepository.existsByNumber(number);
    }

    @Transactional
    public String forCreateCar(CarDto request, User user){
        if(existsByNumber(request.getNumber()))
            return "Автомобиль с таким номером уже существует.";

        Car car = new Car(
                request.getModel(),
                request.getNumber(),
                request.getColor(),
                user
        );
        carRepository.save(car);
        return "Автомобиль создан.";
    }


}
