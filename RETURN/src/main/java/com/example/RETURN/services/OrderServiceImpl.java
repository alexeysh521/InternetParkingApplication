package com.example.RETURN.services;

import com.example.RETURN.dto.*;
import com.example.RETURN.enums.OrderSlotStatus;
import com.example.RETURN.enums.ParkingSlotNumber;
import com.example.RETURN.enums.ParkingSlotSize;
import com.example.RETURN.models.Order;
import com.example.RETURN.models.ParkingSpace;
import com.example.RETURN.models.User;
import com.example.RETURN.repositories.OrderRepository;
import com.example.RETURN.repositories.ParkingRepository;
import com.example.RETURN.repositories.UserRepository;
import com.example.RETURN.services.impl.OrderService;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ParkingRepository parkingRepository;

    private final OrderAndUserUtilsServiceImpl orderAndUserUtilsService;

    private final ModelMapper modelMapper;

    public OrderServiceImpl(OrderRepository orderRepository, UserRepository userRepository,
                            ParkingRepository parkingRepository, OrderAndUserUtilsServiceImpl orderAndUserUtilsService,
                            ModelMapper modelMapper) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.parkingRepository = parkingRepository;
        this.orderAndUserUtilsService = orderAndUserUtilsService;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public void save(Order order){
        orderRepository.save(order);
    }

    public List<InfoOrderDto> forViewOrdersByUser(AnEntityWithAnIdOnlyDto dto){
        User user = userRepository.findById(dto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден."));
        List<Order> orders = orderRepository.findAllOrderByUser(user);

        return orders.stream()
                .map(this::convertToDto)
                .toList();
    }

    public List<InfoOrderDto> forViewMyOrders(User user){
        List<Order> orders = orderRepository.findAllOrderByUser(user);

        if(orders.isEmpty())
            throw new EntityNotFoundException("У вас нет заказов.");

        return orders.stream()
                .map(this::convertToDto)
                .toList();
    }

    @Transactional
    public InfoOrderDto fromCreateOrder(CreateOrderDto orderDto, User user){

        LocalDateTime startTime = orderDto.getStartTime();
        LocalDateTime endTime = orderDto.getEndTime();

        if(Duration.between(startTime, endTime).toMinutes() < 10)
            throw new IllegalArgumentException("Минимальный заказ от 10 минут.");

        String size = orderDto.getSize();

        int price = orderRate(startTime, endTime, size);

        if(!orderAndUserUtilsService.balanceUser(user, price)){//проверка баланса пользователя
            throw new IllegalArgumentException(
                    String.format("Недостаточно средств для оформления заказа стоимостью %d рублей. " +
                    "Пополните счет на %d рублей.", price, Math.abs(user.getBalance() - price))
            );
        }else
            user.setBalance(user.getBalance() - price);

        ParkingSlotSize parkSize = ParkingSlotSize.fromStringParking(size);//получаем объект enum с размером
        List<ParkingSpace> spaces = parkingRepository.findByParkingSlotSize(parkSize);//если есть парковочное место с таким айди, получаем её

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
                startTime,
                endTime
        );

        save(order);
        userRepository.save(user);

        return convertToDto(order);
    }

    public List<InfoOrderDto> allActiveOrdersByUserName(String userName){
        User user = userRepository.findByUserName(userName)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден."));

        LocalDateTime now = LocalDateTime.now();
        List<Order> orders = orderRepository.findAllActiveOrdersAfterNow(user, now);//получаем только активные заказы

        if(orders.isEmpty())
            throw new EntityNotFoundException("Не найдено активных заказов.");

        return orders.stream()
                .map(this::convertToDto)
                .toList();
    }

    @Transactional
    public InfoOrderDto forTerminatedOrder(TerminateOrderDto request, User user){
        int retMon = 0;
        int fine = 0;
        int balance = user.getBalance();
        LocalDateTime now = LocalDateTime.now();
        Order order = orderRepository.findByUserAndId(user, request.getOrderId())
            .filter(o -> o.getOrderSlotStatus() != OrderSlotStatus.COMPLETED)
            .orElseThrow(() -> new EntityNotFoundException(
                "У вас нет заказа с таким id, либо заказ завершен."
            ));

        String size = order.getParking().getParkingSlotSize().name();

        if(order.getEndTime().isAfter(now)) {//частичный возврат средств
            retMon = completeOrderEarly(order.getStartTime(), order.getEndTime(), size);
            user.setBalance(balance + retMon);
        }

        if(order.getOrderSlotStatus() == OrderSlotStatus.OVERDUE){
            fine = orderRate(order.getEndTime(), now, size);
            if(balance < fine)
                throw new IllegalArgumentException("Недостаточно средств для уплаты штрафа " + fine);

            user.setBalance(balance - fine);
        }

        order.getParking().setStatus(true);
        order.setOrderSlotStatus(OrderSlotStatus.COMPLETED);
        //можно реализовать возврат денежных средств.

        InfoOrderDto dto = convertToDto(order);
        dto.setFine(fine);
        dto.setReturnMoney(retMon);

        return dto;
    }

    @Transactional
    public InfoOrderDto forExtendOrder(ExtendOrderDto request, User user){//
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

    private int completeOrderEarly(LocalDateTime start, LocalDateTime end, String size){
        return (int) Math.round(orderRate(start, end, size) * 0.7);
    }

    private int orderRate(LocalDateTime start, LocalDateTime end, String size){//тарифный план
        long hours = Duration.between(start, end).toHours();
        long minutes = Duration.between(start, end).toMinutes();
        int pricePerHours = ParkingSlotSize.getPriceByName(size);
        double price;

        long month = ChronoUnit.MONTHS.between(start, end);

        if(minutes < 60)
            price = minutes * pricePerHours * 0.10;//скидка 90%
        else if(hours <= 24)
            price = hours * pricePerHours;//цена кол-во часов * стоимость часа
        else if(month < 1)
            price = hours * pricePerHours * 0.58; //больше одного дня скидка 42% за час
        else
            price = hours * pricePerHours * 0.34;//месяц и более скидка 66% за час

        return (int) Math.round(price);
    }

    private void extendOrder(Order order, User user, LocalDateTime endTime, LocalDateTime extendTime){
        long hours = Duration.between(endTime, extendTime).toHours();//кол-во часов (разница на сколько мы продлеваем)
        int price = (int) hours * ParkingSlotSize.getPriceByName(order.getParking()
                .getParkingSlotSize().name());//по номеру парковки получаем цену за час и * на кол-во часов разниц

        if(!orderAndUserUtilsService.balanceUser(user, price)){//проверка баланса пользователя
            throw new IllegalArgumentException(String.format(
                    "Недостаточно средств для оформления продления заказа стоимостью %d рублей.\n" +
                            "Пополните счет на %d рублей.", price, Math.abs(user.getBalance() - price)));
        }else
            user.setBalance(user.getBalance() -  price);//списываем деньги со счета

        order.setPrice(order.getPrice() + price);//делаем новую стоимость заказа и сохраняем
        order.setEndTime(extendTime);//сохраняем время до которого продливаем

        save(order);
        userRepository.save(user);
    }

    public List<InfoOrderDto> forAllOrders(){//
        return orderRepository.findAll().stream()
                .map(this::convertToDto)
                .toList();
    }

    public List<InfoOrderDto> forViewActiveOrders(){//
        List<Order> active = orderRepository.findAllActiveOrder();

        return active.stream()
                .map(this::convertToDto)
                .toList();
    }

    public InfoOrderDto convertToDto(Order order){
        return modelMapper.map(order, InfoOrderDto.class);
    }
}
