package com.example.RETURN.scheduler;

import com.example.RETURN.enums.OrderSlotStatus;
import com.example.RETURN.models.Order;
import com.example.RETURN.repositories.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderStatusScheduler {

    private final OrderRepository orderRepository;

    @Scheduled(fixedRate = 30 * 60 * 1000)//обновление каждые 30 минут
    @Transactional
    public void markOverdueOrders(){
        LocalDateTime now = LocalDateTime.now();

        List<Order> overdue = orderRepository.findAllByOrderSlotStatusAndEndTimeBefore(OrderSlotStatus.ACTIVE, now);

        overdue.forEach(order -> order.setOrderSlotStatus(OrderSlotStatus.OVERDUE));

        log.info("Marked {} orders as OVERDUE at {}", overdue.size(), now);//метод .size() возвращает кол во элементов, подвергшимся изменению
    }

}
