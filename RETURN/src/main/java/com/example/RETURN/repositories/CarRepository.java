package com.example.RETURN.repositories;

import com.example.RETURN.models.Car;
import com.example.RETURN.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface CarRepository extends JpaRepository<Car, Integer> {

    boolean existsByNumber(String number);

    Optional<List<Car>> findByUser(User user);

}
