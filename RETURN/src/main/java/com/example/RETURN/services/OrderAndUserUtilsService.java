package com.example.RETURN.services;

import com.example.RETURN.models.Order;
import com.example.RETURN.models.User;
import com.example.RETURN.repositories.OrderRepository;
import com.example.RETURN.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class OrderAndUserUtilsService {

    @Autowired private OrderRepository orderRepository;

    public List<Order> allOrders(){
        return orderRepository.findAll();
    }

    public boolean balance(User user, int value){
        return user.getBalance() >= value;
    }

    //метод для перевода времени в дни часы минуты.
    public String durationTimes(Duration duration){
        StringBuilder time = new StringBuilder();
        long days = duration.toDays();
        long hours = duration.toHours() % 24;
        long minutes = duration.toMinutes() % 60;

        if(days > 0) time.append(days).append(" д ");
        if (hours > 0) time.append(hours).append(" ч ");
        if (minutes > 0) time.append(String.format("%s м", minutes));
        return time.length() != 0 ? time.toString().trim() : "ошибка в методе сервиса";
    }

}
