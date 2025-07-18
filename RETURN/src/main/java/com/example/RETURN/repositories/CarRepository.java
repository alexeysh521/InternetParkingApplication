package com.example.RETURN.repositories;

import com.example.RETURN.models.Car;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CarRepository extends JpaRepository<Car, Integer> {

    boolean existsByNumber(String number);

}
