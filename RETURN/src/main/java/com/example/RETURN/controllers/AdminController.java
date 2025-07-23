package com.example.RETURN.controllers;

import com.example.RETURN.dto.AnEntityWithAnIdOnlyDto;
import com.example.RETURN.dto.CreateParkingSpaceDto;
import com.example.RETURN.dto.UserInfoDto;
import com.example.RETURN.services.OrderService;
import com.example.RETURN.services.ParkingService;
import com.example.RETURN.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/admin")
public class AdminController {

    @Autowired private UserService userService;
    @Autowired private OrderService orderService;
    @Autowired private ParkingService parkingService;

    @GetMapping("/allUsers")//просмотр всех пользователей
    public ResponseEntity<?> allUsers(){
        return ResponseEntity.ok(userService.forAllUsers());
    }

    @GetMapping("/viewActiveOrders")//просмотр только активных заказов
    public ResponseEntity<?> viewActiveOrders(){
        return ResponseEntity.ok(orderService.forViewActiveOrders());
    }

    @GetMapping("/allOrders")//просмотр всех заказов
    public ResponseEntity<?> allOrders(){
        return ResponseEntity.ok(orderService.forAllOrders());
    }

    @PostMapping("/create/parkingSpace")//создать новое парковочное место
    public ResponseEntity<?> createParkSpace(@Valid @RequestBody CreateParkingSpaceDto parkingDto) {
        return ResponseEntity.ok(parkingService.forCreateParkingSpace(parkingDto));
    }

    @DeleteMapping("/deletePS")//удалить парковочное место
    public ResponseEntity<?> deleteParkingSpace(@Valid @RequestBody AnEntityWithAnIdOnlyDto dto){
        return ResponseEntity.ok(parkingService.forDeleteParkingSpace(dto));
    }

}