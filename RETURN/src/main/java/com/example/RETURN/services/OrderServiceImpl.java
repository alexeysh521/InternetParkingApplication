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
import java.util.Objects;

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

    public List<OrderInfoDto> forViewOrdersByUser(AnEntityWithAnIdOnlyDto dto){
        User user = userRepository.findById(dto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не найден."));
        List<Order> orders = orderRepository.findAllOrderByUser(user);

        return orders.stream()
                .map(this::convertToDto)
                .toList();
    }

    public List<OrderInfoDto> forViewMyOrders(User user){
        List<Order> orders = orderRepository.findAllOrderByUser(user);

        if(orders.isEmpty())
            throw new EntityNotFoundException("У вас нет заказов.");

        return orders.stream()
                .map(this::convertToDto)
                .toList();
    }

    private int orderRate(LocalDateTime start, LocalDateTime end, OrderCreateDto dto){//тарифный план
        long hours = Duration.between(start, end).toHours();
        int pricePerHours = ParkingSlotSize.getPriceByName(dto.getSize());
        double price;

        long month = ChronoUnit.MONTHS.between(start, end);

        if(hours <= 24)
            price = hours * pricePerHours;//цена кол-во часов * стоимость часа
        else if(month < 1)
            price = hours * pricePerHours * 0.58; //больше одного дня скидка 42% за час
        else
            price = hours * pricePerHours * 0.34;//месяц и более

        return (int) Math.round(price);
    }

    @Transactional
    public OrderInfoDto fromCreateOrder(OrderCreateDto orderDto, User user){

        int price = orderRate(orderDto.getStartTime(), orderDto.getEndTime(), orderDto);

        if(!orderAndUserUtilsService.balanceUser(user, price)){//проверка баланса пользователя
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

    @Transactional
    public OrderInfoDto forTerminatedOrder(TerminateOrderDto request, User user){
        Order order = orderRepository.findByUserAndId(user, request.getOrderId())
            .filter(o -> o.getOrderSlotStatus() == OrderSlotStatus.ACTIVE ||
                o.getOrderSlotStatus() == OrderSlotStatus.OVERDUE)
            .orElseThrow(() -> new EntityNotFoundException(
                "У вас нет заказов с таким id, либо заказы завершенные."
            ));

        order.getParking().setStatus(true);
        order.setOrderSlotStatus(OrderSlotStatus.COMPLETED);
        //можно реализовать возврат денежных средств.

        return convertToDto(order);
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

    public List<OrderInfoDto> forAllOrders(){//
        return orderRepository.findAll().stream()
                .map(this::convertToDto)
                .toList();
    }

    public List<OrderInfoDto> forViewActiveOrders(){//
        List<Order> active = orderRepository.findAllActiveOrder();

        return active.stream()
                .map(this::convertToDto)
                .toList();
    }

    public OrderInfoDto convertToDto(Order order){
        return modelMapper.map(order, OrderInfoDto.class);
    }


}
