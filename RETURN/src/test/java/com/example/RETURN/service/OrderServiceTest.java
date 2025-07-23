//package com.example.RETURN.service;
//
//import com.example.RETURN.dto.OrderCreateDto;
//import com.example.RETURN.enums.ParkingSlotNumber;
//import com.example.RETURN.enums.ParkingSlotSize;
//import com.example.RETURN.models.Order;
//import com.example.RETURN.models.ParkingSpace;
//import com.example.RETURN.models.User;
//import com.example.RETURN.repositories.OrderRepository;
//import com.example.RETURN.repositories.ParkingRepository;
//import com.example.RETURN.repositories.UserRepository;
//import com.example.RETURN.services.OrderAndUserUtilsServiceImpl;
//import com.example.RETURN.services.OrderServiceImpl;
//import jakarta.persistence.EntityNotFoundException;
//import jakarta.validation.ConstraintViolation;
//import jakarta.validation.Validation;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.junit.jupiter.params.ParameterizedTest;
//import org.junit.jupiter.params.provider.CsvSource;
//import org.mockito.ArgumentCaptor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//
//import java.time.Duration;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//import java.util.Set;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//public class OrderServiceTest {
//
//    @Mock private OrderRepository orderRepository;
//    @Mock private UserRepository userRepository;
//    @Mock private ParkingRepository parkingRepository;
//    @Mock private OrderAndUserUtilsServiceImpl orderAndUserUtilsService;
//
//    @InjectMocks private OrderServiceImpl orderService;
//
//    private final LocalDateTime startTime = LocalDateTime.now();
//    private final LocalDateTime endTime = LocalDateTime.now().plusDays(1);
//
//    public User buildUser(int balance, String name){
//        User user = new User();
//        user.setUserName(name);
//        user.setBalance(balance);
//        return user;
//    }
//
//    public OrderCreateDto buildDto(String size){
//        OrderCreateDto orderDto = new OrderCreateDto();
//        orderDto.setSize(size);
//        orderDto.setStartTime(startTime);
//        orderDto.setEndTime(endTime);
//        return orderDto;
//    }
//
//    public ParkingSpace buildParkingSpace(List<Order> orders){
//        ParkingSpace parkingSpace = new ParkingSpace();
//        parkingSpace.setParkingSlotNumber(ParkingSlotNumber.A1);
//        parkingSpace.setParkingSlotSize(ParkingSlotSize.XL);
//        parkingSpace.setOrders(orders);
//        parkingSpace.setStatus(false);
//        return parkingSpace;
//    }
//
//    @Test
//    void allActiveOrderByUserName_whenValidInput_thenReturnResultMessage(){
//        User user = buildUser(10_000, "Bob");
//        String username = user.getUserName();
//
//        Order order = new Order();
//        order.setUser(user);
//        order.setStartTime(startTime);
//        order.setEndTime(endTime);
//        order.setPrice(10_000);
//        order.setParking(buildParkingSpace(List.of(order)));
//
//        when(userRepository.findByUserName(user.getUserName())).thenReturn(Optional.of(user));
//        when(orderRepository.findAllOrderByUserAndStatusOrderTrue(user)).thenReturn(List.of(order));
//
//        LocalDateTime now = LocalDateTime.now();
//        Duration duration = Duration.between(now, order.getEndTime());
//
//        String result = String.format("""
//
//                                Заказ пользователя %s: статус "Активный"
//                                Характеристики заказа: стоимость %d
//                                Номер парк.места %s, размер %s
//                                Дата регистрации парк.места %s
//                                Договор действителен до %s
//                                Оставшееся "парковочное" время %s.
//                                ____________________________________________
//                                """,
//                username, order.getPrice(), order.getParking().getParkingSlotNumber(),
//                order.getParking().getParkingSlotSize(), order.getStartTime(),
//                order.getEndTime(), orderAndUserUtilsService.durationTimes(duration));
//
//        assertEquals(result, orderService.allActiveOrdersByUserName(user.getUserName()));
//
//        verify(userRepository, times(1)).findByUserName(user.getUserName());
//        verify(orderRepository, times(1)).findAllOrderByUserAndStatusOrderTrue(user);
//    }
//
//    @Test
//    void allActiveOrderByUserName_notFindOrderByUserAndStatusOrderTrue_returnErrorMessage(){
//        User user = buildUser(1_000, "Bob");
//
//        when(userRepository.findByUserName(user.getUserName())).thenReturn(Optional.of(user));
//        when(orderRepository.findAllOrderByUserAndStatusOrderTrue(user)).thenReturn(List.of());
//
//        String errorMessage = "У вас нет активных заказов.";
//
//        assertEquals(errorMessage, orderService.allActiveOrdersByUserName(user.getUserName()));
//
//        verify(userRepository, times(1)).findByUserName(user.getUserName());
//        verify(orderRepository, times(1)).findAllOrderByUserAndStatusOrderTrue(user);
//    }
//
//    @Test
//    void allActiveOrdersByUserName_NotFindUserByUserName_shouldThrow(){
//        User user = buildUser(10_000, "Bob");
//
//        when(userRepository.findByUserName(user.getUserName())).thenReturn(Optional.empty());
//
//        UsernameNotFoundException ex = assertThrows(
//                UsernameNotFoundException.class,
//                () -> orderService.allActiveOrdersByUserName(user.getUserName())
//        );
//
//
//        assertEquals("Пользователь не найден.", ex.getMessage());
//
//        verify(userRepository, times(1)).findByUserName(user.getUserName());
//    }
//
//
//    @Test
//    void fromCreateOrder_whenValidInput_shouldCreateOrderAndReturnConfirmation(){
//        User user = buildUser(10_000, "Maik");
//        OrderCreateDto orderDto = buildDto("XL");
//
//        long hours = Duration.between(orderDto.getStartTime(), orderDto.getEndTime()).toHours();
//        long price = hours * (ParkingSlotSize.getPriceByName(orderDto.getSize()));
//
//        ParkingSpace freeSpace = new ParkingSpace();
//        freeSpace.setStatus(true);
//
//        when(orderAndUserUtilsService.balance(any(), anyInt())).thenReturn(true);
//        when(parkingRepository.findByParkingSlotSize(ParkingSlotSize.XL)).thenReturn(List.of(freeSpace));
//        when(orderRepository.save(any(Order.class))).thenAnswer(inv -> {//then answer - тогда ответить. Invocation(вызов)
//            Order order = inv.getArgument(0);//получить первый аргумент
//            order.setId(1L);//имитация поведения базы данных, что она устанавливает id
//            return order;
//        });
//
//        String result = orderService.fromCreateOrder(orderDto, user);
//
//        ArgumentCaptor<Order> orderCaptor = ArgumentCaptor.forClass(Order.class);//ArgumentCaptor<Order> позволяет поймать реальный объект Order, который был передан в метод save
//        verify(orderRepository).save(orderCaptor.capture());//захвати (capture) объект Order, который мы передали
//
//        assertEquals(user, orderCaptor.getValue().getUser());
//
//        String thisMessage = String.format("Заказ создан: Id заказа: %d, Заказчик: %s, Стоимость: %d рублей.",
//                orderCaptor.getValue().getId(), user.getUserName(), price);
//
//        assertEquals(result, thisMessage);
//
//        verify(userRepository, times(1)).save(user);
//    }
//
//    @Test
//    void orderDtoValidation_shouldFail_whenStartEqualsEndTime(){
//        OrderCreateDto orderDto = new OrderCreateDto();
//        LocalDateTime now = LocalDateTime.now();
//        orderDto.setStartTime(now);
//        orderDto.setEndTime(now);
//
//        Set<ConstraintViolation<OrderCreateDto>> violations = Validation.buildDefaultValidatorFactory()
//                .getValidator()
//                .validate(orderDto);
//
//        assertFalse(violations.isEmpty());
//        assertTrue(violations.stream()
//                .anyMatch(v ->
//                        v.getMessage().equals("Введите корректную дату")));
//    }
//
//    @Test
//    void fromCreateOrder_whenNoParkingFound_shouldThrow(){
//        User user = buildUser(10_000, "Maik");
//        OrderCreateDto orderDto = buildDto("XL");
//
//        when(orderAndUserUtilsService.balance(any(), anyInt())).thenReturn(true);
//        when(parkingRepository.findByParkingSlotSize(ParkingSlotSize.XL)).thenReturn(List.of());//имитация что нет XL парковок -> пустой список
//
//        assertThrows(EntityNotFoundException.class, () -> orderService.fromCreateOrder(orderDto, user));
//    }
//
//    @Test
//    void fromCreateOrder_whenAllSpacesOccupied_shouldThrow(){
//        User user = buildUser(10_000, "Alex");
//        OrderCreateDto orderDto = buildDto("XL");
//
//        ParkingSpace parkingSpace = new ParkingSpace();
//        parkingSpace.setStatus(false);
//
//        when(orderAndUserUtilsService.balance(any(), anyInt())).thenReturn(true);
//        when(parkingRepository.findByParkingSlotSize(ParkingSlotSize.XL)).thenReturn(List.of(parkingSpace));
//
//        assertThrows(EntityNotFoundException.class, () -> orderService.fromCreateOrder(orderDto, user));
//    }
//
//    @Test
//    void fromCreateOrder_whenUserHasNotEnoughBalance_shouldReturnErrorMessage(){
//        User user = buildUser(100, "Maik");
//        OrderCreateDto orderDto = buildDto("XL");
//
//        long hours = Duration.between(orderDto.getStartTime(), orderDto.getEndTime()).toHours();
//        long price = hours * (ParkingSlotSize.getPriceByName(orderDto.getSize()));
//
//        when(orderAndUserUtilsService.balance(any(), anyInt())).thenReturn(false);
//
//        String thisMessage = String.format(
//                "Недостаточно средств для оформления заказа стоимостью %d рублей.\n" +
//                        "Пополните счет на %d рублей.", price, Math.abs(user.getBalance() - (int) price));
//
//        String errorMessage = orderService.fromCreateOrder(orderDto, user);
//
//        assertEquals(errorMessage, thisMessage);
//    }
//
//    @Test
//    void save_ifTheOrderHasBeenSaved_savedOrder(){
//        User user = buildUser(10_000, "Bob");
//        ParkingSpace parkingSpace = new ParkingSpace();
//        Order order = new Order(user, parkingSpace, 1234, startTime, endTime);
//
//        orderService.save(order);
//
//        verify(orderRepository, times(1)).save(order);
//    }
//
//    @ParameterizedTest
//    @CsvSource({
//            "true, Активен",
//            "false, Не активен"
//    })
//    void tryOrFalseFromStrTest(boolean b, String str){
//        assertEquals(str, orderService.truOrFalseFromStr(b));
//    }
//
//    @Test
//    void allOrdersByStatusTest(){
//        List<Order> mockOrders = List.of(new Order(), new Order());
//
//        when(orderRepository.findByStatusOrderTrue()).thenReturn(mockOrders);//подготовка поведения мока
//
//        List<Order> result = orderService.allOrdersByStatusTrue();//вызов метода сервиса
//
//        assertEquals(mockOrders, result);//сравнение результатов
//
//        verify(orderRepository, times(1)).findByStatusOrderTrue();
//    }
//
//}
