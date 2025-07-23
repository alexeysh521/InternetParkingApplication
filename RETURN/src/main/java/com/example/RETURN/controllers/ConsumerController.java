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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/consumer")
public class ConsumerController {

    @Autowired private UserService userService;
    @Autowired private OrderService orderService;
    @Autowired private ParkingService parkingService;
    @Autowired private CarService carService;

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

    @GetMapping("/freeParkSpace")//просмотр, какие места свободны
    public ResponseEntity<?> freeParkingSpace(){
        return ResponseEntity.ok(parkingService.forFreeParkSpace());
    }

    @PostMapping("/create/order")//создать заказ
    public ResponseEntity<?> orderParkingSpace(@Valid @RequestBody OrderCreateDto orderDto,
                                               @AuthenticationPrincipal User user){
        return ResponseEntity.ok(orderService.fromCreateOrder(orderDto, user));
    }

    @PostMapping("/registrationCar")//зарегистрировать свой автомобиль
    public ResponseEntity<?> createCar(@Valid @RequestBody CarDto request,
                                            @AuthenticationPrincipal User user){
        return ResponseEntity.ok(carService.forCreateCar(request, user));
    }

    @PostMapping("/terminateOrder")//завершить заказ по номеру парк.места                                             CHECK
    public ResponseEntity<?> terminatedOrder(@Valid @RequestBody TerminateOrderDto request,
                                                  @AuthenticationPrincipal User user){
        return ResponseEntity.ok(orderService.forTerminatedOrder(request, user));
    }

    @PostMapping("/extendOrder")//продлить заказ                                                                      CHECK
    public ResponseEntity<?> extendOrder(@Valid @RequestBody ExtendOrderDto request,
                                                         @AuthenticationPrincipal User user){
        return ResponseEntity.ok(orderService.forExtendOrder(request, user));
    }

}
