package com.example.RETURN.services.impl;

import com.example.RETURN.dto.*;
import com.example.RETURN.models.Order;
import com.example.RETURN.models.User;

import java.util.List;

public interface OrderService {

    void save(Order order);

    List<InfoOrderDto> forViewOrdersByUser(AnEntityWithAnIdOnlyDto dto);

    InfoOrderDto fromCreateOrder(CreateOrderDto orderDto, User user);

    List<InfoOrderDto> allActiveOrdersByUserName(String userName);

    InfoOrderDto forTerminatedOrder(TerminateOrderDto request, User user);

    InfoOrderDto forExtendOrder(ExtendOrderDto request, User user);

    List<InfoOrderDto> forAllOrders();

    List<InfoOrderDto> forViewActiveOrders();

    InfoOrderDto convertToDto(Order order);

}
