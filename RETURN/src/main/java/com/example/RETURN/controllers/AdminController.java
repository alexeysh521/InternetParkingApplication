package com.example.RETURN.controllers;

import com.example.RETURN.dto.AnEntityWithAnIdOnlyDto;
import com.example.RETURN.dto.CreateParkingSpaceDto;
import com.example.RETURN.services.CarServiceImpl;
import com.example.RETURN.services.OrderServiceImpl;
import com.example.RETURN.services.ParkingServiceImpl;
import com.example.RETURN.services.UserServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin")
public class AdminController {

    private final UserServiceImpl userService;
    private final OrderServiceImpl orderService;
    private final ParkingServiceImpl parkingService;
    private final CarServiceImpl carService;

    public AdminController(UserServiceImpl userService, OrderServiceImpl orderService,
                           ParkingServiceImpl parkingService, CarServiceImpl carService) {
        this.userService = userService;
        this.orderService = orderService;
        this.parkingService = parkingService;
        this.carService = carService;
    }

    @GetMapping("/all/users")//просмотр всех пользователей
    public ResponseEntity<?> allUsers(){
        return ResponseEntity.ok(userService.forAllUsers());
    }

    @GetMapping("/view/active/orders")//просмотр только активных заказов
    public ResponseEntity<?> viewActiveOrders(){
        return ResponseEntity.ok(orderService.forViewActiveOrders());
    }

    @GetMapping("/view/all/orders")//просмотр всех заказов
    public ResponseEntity<?> allOrders(){
        return ResponseEntity.ok(orderService.forAllOrders());
    }

    @PostMapping("/create/parkingSpace")//создать новое парковочное место
    public ResponseEntity<?> createParkSpace(@Valid @RequestBody CreateParkingSpaceDto parkingDto) {
        return ResponseEntity.ok(parkingService.forCreateParkingSpace(parkingDto));
    }

    @DeleteMapping("/delete/ParkingSpace")//удалить парковочное место
    public ResponseEntity<?> deleteParkingSpace(@Valid @RequestBody AnEntityWithAnIdOnlyDto dto){
        return ResponseEntity.ok(parkingService.forDeleteParkingSpace(dto));
    }

    @PostMapping("/view/orders/user")//метод для просмотра всех заказов конкретного пользователя
    public ResponseEntity<?> viewOrdersByUser(@Valid @RequestBody AnEntityWithAnIdOnlyDto dto){
        return ResponseEntity.ok(orderService.forViewOrdersByUser(dto));
    }

    @GetMapping("/view/cars")//метод для просмотра всех зарегистрированных автомобилей
    public ResponseEntity<?> viewCars(){
        return ResponseEntity.ok(carService.findAll());
    }

    @PostMapping("/view/cars/user")//метод для просмотра всех автомобилей конкретного пользователя
    public ResponseEntity<?> viewCarsByUser(@Valid @RequestBody AnEntityWithAnIdOnlyDto dto){
        return ResponseEntity.ok(carService.findByUser(dto.getId()));
    }

}