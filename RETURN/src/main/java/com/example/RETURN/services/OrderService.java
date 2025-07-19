package com.example.RETURN.services;

import com.example.RETURN.dto.OrderDto;
import com.example.RETURN.enums.ParkingSlotSize;
import com.example.RETURN.models.Order;
import com.example.RETURN.models.ParkingSpace;
import com.example.RETURN.models.User;
import com.example.RETURN.repositories.OrderRepository;
import com.example.RETURN.repositories.ParkingRepository;
import com.example.RETURN.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class OrderService {

    @Autowired private OrderRepository orderRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ParkingRepository parkingRepository;
    @Autowired private OrderAndUserUtilsService orderAndUserUtilsService;

    @Transactional
    public void save(Order order){
        orderRepository.save(order);
    }

    public String truOrFalseFromStr(boolean b){
        return b ? "Активен" : "Не активен";
    }

    public List<Order> allOrdersByStatusTrue(){
        return orderRepository.findByStatusOrderTrue();
    }

    @Transactional
    public String fromCreateOrder(OrderDto orderDto, User user){
        long hours = Duration.between(orderDto.getStartTime(), orderDto.getEndTime()).toHours();//кол-во часов
        long price = hours * (ParkingSlotSize.getPriceByName(orderDto.getSize()));//цена кол-во часов * стоимость часа

        if(!orderAndUserUtilsService.balance(user, (int) price)){//проверка баланса пользователя
            return String.format(
                    "Недостаточно средств для оформления заказа стоимостью %d рублей.\n" +
                            "Пополните счет на %d рублей.", price, Math.abs(user.getBalance() - (int) price));
        }else
            user.setBalance(user.getBalance() - (int) price);

        ParkingSlotSize size = ParkingSlotSize.fromStringParking(orderDto.getSize());//получаем объект enum с размером
        List<ParkingSpace> spaces = parkingRepository.findByParkingSlotSize(size);//если есть парковочное место с таким айди, получаем её

        if(spaces.isEmpty())
            throw new EntityNotFoundException("Парковочное место не найдено.");

        ParkingSpace freeSpace = spaces.stream()//по полю статус ищем первую свободную парковку
                .filter(ParkingSpace::isStatus)
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Свободного парковочного места нет"));
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

        return String.format("Заказ создан: Id заказа: %d, Заказчик: %s, Стоимость: %d рублей.",
                order.getId(), user.getUserName(), price);
    }

    @Transactional
    public String allActiveOrdersByUserName(String username){
        User user = userRepository.findByUserName(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден."));
        List<Order> orders = orderRepository.findAllOrderByUserAndStatusOrderTrue(user);//получаем только активные заказы

        if(orders.isEmpty()) return "У вас нет активных заказов.";//проверка есть ли вообще активные заказы

        LocalDateTime now = LocalDateTime.now();
        StringBuilder result = new StringBuilder();
        for(Order order : orders){
            Duration duration = Duration.between(now, order.getEndTime());
            if(order.getEndTime().isAfter(now)){
                result.append(String.format("""
                                
                                Заказ пользователя %s: статус "Активный"
                                Характеристики заказа: стоимость %d
                                Номер парк.места %s, размер %s
                                Дата регистрации парк.места %s
                                Договор действителен до %s
                                Оставшееся "парковочное" время %s.
                                ____________________________________________
                                """,
                        username, order.getPrice(), order.getParking().getParkingSlotNumber(),
                        order.getParking().getParkingSlotSize(), order.getStartTime(),
                        order.getEndTime(), orderAndUserUtilsService.durationTimes(duration)));
            }
        }

        return result.toString().trim();
    }

    //по имени пользователя проверяем действителен ли еще его заказ, и если да вернем пользователя
    public User checkRemainedTime(String username) {
        return userRepository.findByUserName(username).orElseThrow(()
                -> new UsernameNotFoundException("Пользователь не найден"));
    }

}
