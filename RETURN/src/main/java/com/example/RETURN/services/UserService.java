package com.example.RETURN.services;

import com.example.RETURN.dto.DepositDto;
import com.example.RETURN.dto.ExtendOrderDto;
import com.example.RETURN.dto.TerminateOrderDto;
import com.example.RETURN.enums.ParkingSlotNumber;
import com.example.RETURN.enums.ParkingSlotSize;
import com.example.RETURN.models.Order;
import com.example.RETURN.models.User;
import com.example.RETURN.repositories.OrderRepository;
import com.example.RETURN.repositories.ParkingRepository;
import com.example.RETURN.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserService {

    @Autowired private UserRepository userRepository;
    @Autowired private OrderRepository orderRepository;
    @Autowired private ParkingRepository parkingRepository;
    @Autowired private OrderAndUserUtilsService orderAndUserUtilsService;
    @Autowired private OrderService orderService;

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
    public String forBalanceOperation(DepositDto depositDto, User user){
        switch (depositDto.getNameOperation().toUpperCase()){
            case "ПОПОЛНИТЬ":
                if(depositDto.getBalance() == null || depositDto.getBalance() < 10 || depositDto.getBalance() > 50000)
                    return "Пополнять можно от 10 до 50.000 рублей.";
                user.setBalance(user.getBalance() + depositDto.getBalance());
                break;
            case "СНЯТЬ":
                if(depositDto.getBalance() == null || user.getBalance() < depositDto.getBalance())
                    return "На вашем счете недостаточно средств";
                user.setBalance(user.getBalance() - depositDto.getBalance());
                break;
            case "ПРОВЕРИТЬ":
                if (user.getBalance() == 0)
                    return "Ваш баланс 0 рублей.";
                return String.format("Успешно, ваш баланс %d рублей.", user.getBalance());
            default:
                return "Критическая ошибка";
        }

        save(user);

        return String.format("Успешно, ваш баланс %d рублей. Пользователь %s",
                user.getBalance(), user.getUsername());
    }

    public String forAllOrders(){
        StringBuilder result = new StringBuilder();
        boolean flag = false;

        LocalDateTime now = LocalDateTime.now();
        for(Order order : orderAndUserUtilsService.allOrders()){
            Duration duration = Duration.between(now, order.getEndTime());
            flag = true;
            result.append(String.format("""
                               
                               Заказ %d пользователя %s: статус "%s"
                               Характеристики заказа: стоимость %d
                               Номер парк.места %s, размер парк.места %s
                               Дата регистрации парк.места %s
                               Договор действителен до %s
                               %s
                               ____________________________________________
                               """,
                    order.getId(), order.getUser().getUserName(),
                    orderService.truOrFalseFromStr(order.isStatusOrder()),
                    order.getPrice(), order.getParking().getParkingSlotNumber(),
                    order.getParking().getParkingSlotSize().name(), order.getStartTime(),
                    order.getEndTime(),
                    order.isStatusOrder() ? ("Оставшееся \"парковочное\" время "
                            + orderAndUserUtilsService.durationTimes(duration)) : ""
                    ));
        }

        return !flag ? "Заказов нет." : result.toString().trim();
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

    public String forViewActiveOrders(){
        StringBuilder result = new StringBuilder();
        LocalDateTime now = LocalDateTime.now();
        boolean flag = false;
        for(Order order : orderRepository.findByStatusOrderTrue()){
            Duration duration = Duration.between(now, order.getEndTime());
            flag = true;
            result.append(String.format("""
                               
                               Заказ пользователя %s: статус "Активный"
                               Характеристики заказа: стоимость %d
                               Номер парк.места %s, размер парк.места %s
                               Дата регистрации парк.места %s
                               Договор действителен до %s
                               Оставшееся "парковочное" время %s.
                               ____________________________________________
                               """,
                    order.getUser().getUserName(), order.getPrice(), order.getParking().getParkingSlotNumber(),
                    order.getParking().getParkingSlotSize().name(), order.getStartTime(),
                    order.getEndTime(), orderAndUserUtilsService.durationTimes(duration)));
        }

        return !flag ? "Заказов нет." : result.toString().trim();
    }

    @Transactional
    public String forTerminatedOrder(TerminateOrderDto request, User user){
        if(!existsOrderByUsername(user.getUserName()))//проверка если у пользователя заказы
            return "У Вас нет заказов.";
        List<Order> orders = orderRepository.findAllByUserWithParking(user);
        StringBuilder result = new StringBuilder();

        for (Order order : orders) {
            if (order.getParking().getParkingSlotNumber() == ParkingSlotNumber.fromStringNumber(request.getNumber())){
                order.setStatusOrder(false);
                order.getParking().setStatus(true);
                orderRepository.save(order);
                parkingRepository.save(order.getParking());
                result.append(String.format("Освобождено место №%s размера %s",
                        order.getParking().getParkingSlotNumber().name(),
                        order.getParking().getParkingSlotSize().name()));
            }
        }

        if(result.isEmpty())
            return "Парковка не найдена";

        return result.toString().trim();
    }

    @Transactional
    public String forExtendOrder(ExtendOrderDto request, User user){
        //найти активные заказы пользователя
        List<Order> orders = orderRepository.findAllByUserWithParking(user);
        if (orders.isEmpty())//проверка есть ли такие заказы
            return "У вас нет активных заказов с номеру парковочного места"+request.getNumber();

        StringBuilder result = new StringBuilder();
        boolean flag = false;

        for(Order order : orders){
            if(order.getParking().getParkingSlotNumber() == ParkingSlotNumber.fromStringNumber(request.getNumber())){
                flag = true;
                long hours = Duration.between(order.getEndTime(), request.getExtendTime()).toHours();//кол-во часов (разница на сколько мы продлеваем)
                long price = hours * ParkingSlotSize.getPriceByName(order.getParking().getParkingSlotSize().name());//по номеру парковки получаем цену за час и * на кол-во часов разниц

                if(order.getEndTime().isEqual(request.getExtendTime()))
                    return "Дата продления заказа совпадает с датой конца, указанной в вашем заказе.";

                if(!orderAndUserUtilsService.balance(user, (int) price)){//проверка баланса пользователя
                    return String.format(
                            "Недостаточно средств для оформления продления заказа стоимостью %d рублей.\n" +
                                    "Пополните счет на %d рублей.", price, Math.abs(user.getBalance() - (int) price));
                }else
                    user.setBalance(user.getBalance() - (int) price);//списываем деньги со счета

                order.setEndTime(request.getExtendTime());//сохраняем время до которого продливаем
                order.setPrice(order.getPrice() + price);//делаем новую стоимость заказа и сохраняем

                orderRepository.save(order);
                save(user);

                result.append(String.format(
                    """
                    Статус: Успех!
                    Заказ пользователя %s продлен до %s.
                    Номер/размер парковочного места %s / %s.
                    Стоимость услуги %d.
                    """,
                    user.getUserName(), request.getExtendTime(),
                        order.getParking().getParkingSlotNumber().name(),
                        order.getParking().getParkingSlotSize().name(),
                        price
                ));
                break;
            }
        }

        return flag ? result.toString().trim() : String.format(
                "Заказ пользователя %s не найден по номеру парк места %s. ",
                user.getUserName(),request.getNumber());
    }

}
