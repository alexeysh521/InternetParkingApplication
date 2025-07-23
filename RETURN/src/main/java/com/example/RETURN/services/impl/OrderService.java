package com.example.RETURN.services.impl;

import com.example.RETURN.dto.*;
import com.example.RETURN.models.Order;
import com.example.RETURN.models.User;

import java.util.List;

public interface OrderService {

    void save(Order order);

    List<OrderInfoDto> forViewOrdersByUser(AnEntityWithAnIdOnlyDto dto);

    OrderInfoDto fromCreateOrder(OrderCreateDto orderDto, User user);

    List<OrderInfoDto> allActiveOrdersByUserName(String userName);

    OrderInfoDto forTerminatedOrder(TerminateOrderDto request, User user);

    OrderInfoDto forExtendOrder(ExtendOrderDto request, User user);

    List<OrderInfoDto> forAllOrders();

    List<OrderInfoDto> forViewActiveOrders();

    OrderInfoDto convertToDto(Order order);

}
