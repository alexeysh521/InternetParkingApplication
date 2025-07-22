package com.example.RETURN.services;

import com.example.RETURN.dto.*;
import com.example.RETURN.enums.DepositDtoSlotBalance;
import com.example.RETURN.enums.OrderSlotStatus;
import com.example.RETURN.enums.ParkingSlotNumber;
import com.example.RETURN.enums.ParkingSlotSize;
import com.example.RETURN.models.Order;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UserService {

    @Autowired private UserRepository userRepository;
    @Autowired private OrderRepository orderRepository;
    @Autowired private ParkingRepository parkingRepository;
    @Autowired private OrderAndUserUtilsService orderAndUserUtilsService;

    @Autowired private ModelMapper modelMapper;

    @Transactional
    public void save(User user){
        userRepository.save(user);
    }

    public boolean existsByUserName(String userName){
        return userRepository.existsByUserName(userName);
    }

    public User findById(long id){
        return userRepository.findById(id).orElseThrow(()
                -> new UsernameNotFoundException("Пользователь не найден"));
    }

    public User findByUserName(String userName){
        return userRepository.findByUserName(userName)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден"));
    }

    public boolean existsOrderByUsername(String name){
        return userRepository.existsOrderByUserName(name);
    }

    public List<User> userList(){
        return userRepository.findAll();
    }

    @Transactional
    public AccountOperationDto forBalanceOperation(AccountOperationDto depositDto, User user){
        int amount = depositDto.getBalance();

        switch(DepositDtoSlotBalance.getOperationFromString(depositDto.getNameOperation())){
            case CHECK -> {
                return new AccountOperationDto("CHECK", user.getBalance(), user.getUserName());
            }
            case WITHDRAW -> {
                int newBalance = user.getBalance() - amount;
                if(newBalance < 0) throw new IllegalArgumentException("Недостаточно средств.");
                user.setBalance(newBalance);
                save(user);
                return new AccountOperationDto("WITHDRAW", newBalance, user.getUserName());
            }
            case DEPOSIT -> {
                int newBalance = user.getBalance() + amount;
                user.setBalance(newBalance);
                save(user);
                return new AccountOperationDto("DEPOSIT", newBalance, user.getUserName());
            }
            default ->
                throw new IllegalArgumentException("Неверный ввод.");
        }
    }

    public String forAllOrders(){//                                                                                      RECYCLE
//        StringBuilder result = new StringBuilder();
//        boolean flag = false;
//
//        LocalDateTime now = LocalDateTime.now();
//        for(Order order : orderAndUserUtilsService.allOrders()){
//            Duration duration = Duration.between(now, order.getEndTime());
//            flag = true;
//            result.append(String.format("""
//
//                               Заказ %d пользователя %s: статус "%s"
//                               Характеристики заказа: стоимость %d
//                               Номер парк.места %s, размер парк.места %s
//                               Дата регистрации парк.места %s
//                               Договор действителен до %s
//                               %s
//                               ____________________________________________
//                               """,
//                    order.getId(), order.getUser().getUserName(),
//                    orderService.truOrFalseFromStr(order.isStatusOrder()),
//                    order.getPrice(), order.getParking().getParkingSlotNumber(),
//                    order.getParking().getParkingSlotSize().name(), order.getStartTime(),
//                    order.getEndTime(),
//                    order.isStatusOrder() ? ("Оставшееся \"парковочное\" время "
//                            + orderAndUserUtilsService.durationTimes(duration)) : ""
//                    ));
//        }
//
//        return !flag ? "Заказов нет." : result.toString().trim();
        return null;
    }

    public String forAllUsers(){
        StringBuilder result = new StringBuilder();
        List<User> users = userList();

        if(users.isEmpty()) return result.append("Пользователей нет.").toString();
        result.append("Данные пользователей:\n\n");
        for(User user : users) {
            result.append(String.format("""
                            
                            Имя пользователя %s
                            id пользователя %d
                            Email адрес пользователя %s
                            Роль пользователя %s
                            Баланс пользователя %d
                            ____________________________________________
                            """,
                    user.getUserName(), user.getId(), user.getEmail(), user.getRole(), user.getBalance()
            ));
        }
        return result.toString().trim();
    }

    public String forViewActiveOrders(){//                                                                               RECYCLE
        StringBuilder result = new StringBuilder();
        LocalDateTime now = LocalDateTime.now();
        boolean flag = false;
//        for(Order order : orderRepository.findByStatusOrderTrue()){
//            Duration duration = Duration.between(now, order.getEndTime());
//            flag = true;
//            result.append(String.format("""
//
//                               Заказ пользователя %s: статус "Активный"
//                               Характеристики заказа: стоимость %d
//                               Номер парк.места %s, размер парк.места %s
//                               Дата регистрации парк.места %s
//                               Договор действителен до %s
//                               Оставшееся "парковочное" время %s.
//                               ____________________________________________
//                               """,
//                    order.getUser().getUserName(), order.getPrice(), order.getParking().getParkingSlotNumber(),
//                    order.getParking().getParkingSlotSize().name(), order.getStartTime(),
//                    order.getEndTime(), orderAndUserUtilsService.durationTimes(duration)));
//        }

        return !flag ? "Заказов нет." : result.toString().trim();
    }

    @Transactional
    public OrderInfoDto forTerminatedOrder(TerminateOrderDto request, User user){//

        ParkingSlotNumber requestSlot = ParkingSlotNumber.fromStringNumber(request.getNumber());
        List<Order> orders = orderRepository.findAllByUserWithParking(user);

        return orders.stream()
                .filter(order -> order.getParking().getParkingSlotNumber() == requestSlot)
                .peek(order -> {
                    order.getParking().setStatus(true);
                    order.setOrderSlotStatus(OrderSlotStatus.COMPLETED);
                })
                .map(this::convertToDto)
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("У вас нет заказов на это место."));
    }

    @Transactional
    public OrderInfoDto forExtendOrder(ExtendOrderDto request, User user){//
        //найти активные заказы пользователя
        List<Order> orders = orderRepository.findAllByUserWithParking(user);

        LocalDateTime extend = request.getExtendTime();

        ParkingSlotNumber requestSlot = ParkingSlotNumber.fromStringNumber(request.getNumber());

        Order order = orders.stream()
                .filter(order1 -> order1.getParking().getParkingSlotNumber() == requestSlot)
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("У вас нет заказов с номером " + requestSlot));

        if(!extend.isAfter(order.getEndTime()) || extend.isEqual(order.getEndTime()))
            throw new IllegalArgumentException("Введите корректную дату продления.");

        extendOrder(order, user, order.getEndTime(), extend);

        return convertToDto(order);
    }

    private void extendOrder(Order order, User user, LocalDateTime endTime, LocalDateTime extendTime){
        long hours = Duration.between(endTime, extendTime).toHours();//кол-во часов (разница на сколько мы продлеваем)
        int price = (int) hours * ParkingSlotSize.getPriceByName(order.getParking()
                .getParkingSlotSize().name());//по номеру парковки получаем цену за час и * на кол-во часов разниц

        if(!orderAndUserUtilsService.balance(user, price)){//проверка баланса пользователя
            throw new IllegalArgumentException(String.format(
                    "Недостаточно средств для оформления продления заказа стоимостью %d рублей.\n" +
                            "Пополните счет на %d рублей.", price, Math.abs(user.getBalance() - price)));
        }else
            user.setBalance(user.getBalance() -  price);//списываем деньги со счета

        order.setPrice(order.getPrice() + price);//делаем новую стоимость заказа и сохраняем
        order.setEndTime(extendTime);//сохраняем время до которого продливаем

        orderRepository.save(order);
        save(user);
    }

    public OrderInfoDto convertToDto(Order order){
        return modelMapper.map(order, OrderInfoDto.class);
    }

}
