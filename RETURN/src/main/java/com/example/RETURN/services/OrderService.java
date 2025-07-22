package com.example.RETURN.services;

import com.example.RETURN.dto.CarDto;
import com.example.RETURN.dto.OrderCreateDto;
import com.example.RETURN.dto.OrderInfoDto;
import com.example.RETURN.dto.UserInfoDto;
import com.example.RETURN.enums.ParkingSlotSize;
import com.example.RETURN.models.Car;
import com.example.RETURN.models.Order;
import com.example.RETURN.models.ParkingSpace;
import com.example.RETURN.models.User;
import com.example.RETURN.repositories.OrderRepository;
import com.example.RETURN.repositories.ParkingRepository;
import com.example.RETURN.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class OrderService {

    @Autowired private OrderRepository orderRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ParkingRepository parkingRepository;
    @Autowired private OrderAndUserUtilsService orderAndUserUtilsService;

    @Autowired private ModelMapper modelMapper;
    @Autowired private CarService carService;

    @Transactional
    public void save(Order order){
        orderRepository.save(order);
    }

    public String truOrFalseFromStr(boolean b){
        return b ? "Активен" : "Не активен";
    }

    @Transactional
    public OrderInfoDto fromCreateOrder(OrderCreateDto orderDto, User user){
        long hours = Duration.between(orderDto.getStartTime(), orderDto.getEndTime()).toHours();//кол-во часов
        int price = (int) hours * (ParkingSlotSize.getPriceByName(orderDto.getSize()));//цена кол-во часов * стоимость часа

        if(!orderAndUserUtilsService.balance(user, price)){//проверка баланса пользователя
            throw new IllegalArgumentException(
                    String.format("Недостаточно средств для оформления заказа стоимостью %d рублей. " +
                    "Пополните счет на %d рублей.", price, Math.abs(user.getBalance() - price))
            );
        }else
            user.setBalance(user.getBalance() - price);

        ParkingSlotSize size = ParkingSlotSize.fromStringParking(orderDto.getSize());//получаем объект enum с размером
        List<ParkingSpace> spaces = parkingRepository.findByParkingSlotSize(size);//если есть парковочное место с таким айди, получаем её

        if(spaces.isEmpty())
            throw new EntityNotFoundException("Парковочное место не найдено.");

        ParkingSpace freeSpace = spaces.stream()//по полю статус ищем первую свободную парковку
                .filter(ParkingSpace::isStatus)
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Свободного парковочного места нет."));
        freeSpace.setStatus(false);//теперь место занято

        Order order = new Order(
                user,
                freeSpace,
                price,
                orderDto.getStartTime(),
                orderDto.getEndTime()
        );

        save(order);
        userRepository.save(user);

        return convertToDto(order);
    }

    public List<OrderInfoDto> allActiveOrdersByUserName(String userName){
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден."));

        LocalDateTime now = LocalDateTime.now();
        List<Order> orders = orderRepository.findAllActiveOrdersAfterNow(user, now);//получаем только активные заказы

        return orders.stream()
                .map(this::convertToDto)
                .toList();
    }

    //по имени пользователя проверяем действителен ли еще его заказ, и если да вернем пользователя
    public User checkRemainedTime(String username) {
        return userRepository.findByUserName(username).orElseThrow(()
                -> new UsernameNotFoundException("Пользователь не найден"));
    }

    public OrderInfoDto convertToDto(Order order){
        return modelMapper.map(order, OrderInfoDto.class);
    }


}
