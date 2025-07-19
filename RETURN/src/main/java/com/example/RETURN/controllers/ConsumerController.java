package com.example.RETURN.controllers;

import com.example.RETURN.dto.*;
import com.example.RETURN.models.Order;
import com.example.RETURN.models.User;
import com.example.RETURN.services.CarService;
import com.example.RETURN.services.OrderService;
import com.example.RETURN.services.ParkingService;
import com.example.RETURN.services.UserService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/consumer")
public class ConsumerController {

    @Autowired private UserService userService;
    @Autowired private OrderService orderService;
    @Autowired private ParkingService parkingService;
    @Autowired private CarService carService;

    @GetMapping("/checkOverParkSpace")//посмотреть, сколько времени осталось до конца парковки.
    public ResponseEntity<String> checkOverdueParkingSpace(@AuthenticationPrincipal User user){
        return ResponseEntity.ok(orderService.allActiveOrdersByUserName(user.getUserName()));
    }

    @PostMapping("/balanceOperation")//положить/проверить/снять деньги со счета
    public ResponseEntity<String> balanceOperation(@Valid @RequestBody DepositDto depositDto,
                                              @AuthenticationPrincipal User user){
        return ResponseEntity.ok(userService.forBalanceOperation(depositDto, user));
    }

    @GetMapping("/freeParkSpace")//просмотр, какие места свободны
    public ResponseEntity<String> freeParkingSpace(){
        return ResponseEntity.ok(parkingService.forFreeParkSpace());
    }

    @PostMapping("/create/order")//создать заказ
    public ResponseEntity<String> orderParkingSpace(@Valid @RequestBody OrderDto orderDto,
                                               @AuthenticationPrincipal User user){
        return ResponseEntity.ok(orderService.fromCreateOrder(orderDto, user));
    }

    @PostMapping("/registrationCar")//зарегистрировать свой автомобиль
    public ResponseEntity<String> createCar(@Valid @RequestBody CarDto request,
                                            @AuthenticationPrincipal User user){
        return ResponseEntity.ok(carService.forCreateCar(request, user));
    }

    @PostMapping("/terminateOrder")//завершить заказ по номеру парк.места
    public ResponseEntity<String> terminatedOrder(@Valid @RequestBody TerminateOrderDto request,
                                                  @AuthenticationPrincipal User user){
        return ResponseEntity.ok(userService.forTerminatedOrder(request, user));
    }

    @PostMapping("/extendOrder")//продлить заказ
    public ResponseEntity<String> extendOrder(@Valid @RequestBody ExtendOrderDto request,
                                                         @AuthenticationPrincipal User user){
        return ResponseEntity.ok(userService.forExtendOrder(request, user));
    }

    public Order convertToOrder(OrderDto orderDto){//тестовый Не рабочий метод конвертации, позаимствованный у Алишева.
        ModelMapper modelMapper = new ModelMapper();
        return modelMapper.map(orderDto, Order.class);
    }



}
