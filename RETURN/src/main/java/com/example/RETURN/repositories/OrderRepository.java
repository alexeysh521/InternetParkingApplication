package com.example.RETURN.repositories;

import com.example.RETURN.models.Order;
import com.example.RETURN.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {

    boolean existsByUser(User user);

    List<Order> findAllByUser(User user);

    @Query("SELECT o FROM Order o WHERE o.user = :user AND o.orderSlotStatus = 'ACTIVE' AND o.endTime > :now")
    List<Order> findAllActiveOrdersAfterNow(@Param("user") User user, @Param("now") LocalDateTime now);

    @Query("SELECT o FROM Order o JOIN FETCH o.parking WHERE o.user = :user")
    List<Order> findAllByUserWithParking(@Param("user") User user);

    @Query("SELECT o FROM Order o WHERE o.orderSlotStatus = 'ACTIVE'")
    List<Order> findAllActiveOrder();

    List<Order> findAllOrderByUser(User user);

}
