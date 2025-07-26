package com.example.RETURN.controllers;

import com.example.RETURN.dto.*;
import com.example.RETURN.models.User;
import com.example.RETURN.services.CarServiceImpl;
import com.example.RETURN.services.OrderServiceImpl;
import com.example.RETURN.services.ParkingServiceImpl;
import com.example.RETURN.services.UserServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/consumer")
public class ConsumerController {

    private final UserServiceImpl userService;
    private final OrderServiceImpl orderService;
    private final ParkingServiceImpl parkingService;
    private final CarServiceImpl carService;

    public ConsumerController(UserServiceImpl userService, OrderServiceImpl orderService,
                              ParkingServiceImpl parkingService, CarServiceImpl carService) {
        this.userService = userService;
        this.orderService = orderService;
        this.parkingService = parkingService;
        this.carService = carService;
    }

    @GetMapping("/checkOverParkSpace")//посмотреть, сколько времени осталось до конца парковки.
    public ResponseEntity<?> checkOverdueParkingSpace(@AuthenticationPrincipal User user){
        List<OrderInfoDto> activeOrderInfoDto = orderService.allActiveOrdersByUserName(user.getUserName());
        if(activeOrderInfoDto.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Не найдено заказов.");
        return ResponseEntity.ok(activeOrderInfoDto);
    }

    @PostMapping("/balanceOperation")//положить/проверить/снять деньги со счета
    public ResponseEntity<?> balanceOperation(@Valid @RequestBody AccountOperationDto depositDto,
                                              @AuthenticationPrincipal User user){
        return ResponseEntity.ok(userService.forBalanceOperation(depositDto, user));
    }

    @GetMapping("/free/parkSpace")//просмотр, какие места свободны
    public ResponseEntity<?> freeParkingSpace(){
        return ResponseEntity.ok(parkingService.forFreeParkSpace());
    }

    @PostMapping("/create/order")//создать заказ
    public ResponseEntity<?> orderParkingSpace(@Valid @RequestBody OrderCreateDto orderDto,
                                               @AuthenticationPrincipal User user){
        return ResponseEntity.ok(orderService.fromCreateOrder(orderDto, user));
    }

    @PostMapping("/registration/car")//зарегистрировать свой автомобиль
    public ResponseEntity<?> createCar(@Valid @RequestBody CarDto request,
                                            @AuthenticationPrincipal User user){
        return ResponseEntity.ok(carService.forCreateCar(request, user));
    }

    @GetMapping("/view/myOrders")//просмотр заказов пользователя
    public ResponseEntity<?> viewMyOrders(@AuthenticationPrincipal User user){
        return ResponseEntity.ok(orderService.forViewMyOrders(user));
    }

    @PostMapping("/terminate/order")//завершить заказ по id заказа
    public ResponseEntity<?> terminatedOrder(@Valid @RequestBody TerminateOrderDto request,
                                                  @AuthenticationPrincipal User user){
        return ResponseEntity.ok(orderService.forTerminatedOrder(request, user));
    }

    @PostMapping("/extend/order")//продлить заказ
    public ResponseEntity<?> extendOrder(@Valid @RequestBody ExtendOrderDto request,
                                                         @AuthenticationPrincipal User user){
        return ResponseEntity.ok(orderService.forExtendOrder(request, user));
    }

}
