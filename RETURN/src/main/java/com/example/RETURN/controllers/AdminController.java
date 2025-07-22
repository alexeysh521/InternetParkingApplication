package com.example.RETURN.controllers;

import com.example.RETURN.dto.AnEntityWithAnIdOnlyDto;
import com.example.RETURN.dto.CreateParkingSpaceDto;
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
    @Autowired private ParkingService parkingService;

    @GetMapping("/allUsers")//просмотр всех пользователей                                                             RECYCLE
    public ResponseEntity<String> allUsers(){
        return ResponseEntity.ok(userService.forAllUsers());
    }

    @GetMapping("/viewActiveOrders")//просмотр только активных заказов                                                RECYCLE
    public ResponseEntity<String> viewActiveOrders(){
        return ResponseEntity.ok(userService.forViewActiveOrders());
    }

    @GetMapping("/allOrders")//просмотр всех заказов                                                                  RECYCLE
    public ResponseEntity<String> allOrders(){
        return ResponseEntity.ok(userService.forAllOrders());
    }

    @PostMapping("/create/parkingSpace")//создать новое парковочное место                                             RECYCLE
    public ResponseEntity<String> createParkSpace(@Valid @RequestBody CreateParkingSpaceDto parkingDto) {
        return ResponseEntity.ok(parkingService.forCreateParkingSpace(parkingDto));
    }

    @DeleteMapping("/deletePS")//удалить парковочное место                                                            RECYCLE
    public ResponseEntity<String> deleteParkingSpace(@Valid @RequestBody AnEntityWithAnIdOnlyDto dto){
        return ResponseEntity.ok(parkingService.forDeleteParkingSpace(dto));
    }

}