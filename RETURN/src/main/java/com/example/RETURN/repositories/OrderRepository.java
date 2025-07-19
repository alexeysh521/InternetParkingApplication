package com.example.RETURN.repositories;

import com.example.RETURN.models.Order;
import com.example.RETURN.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {

    boolean existsByUser(User user);

    List<Order> findByStatusOrderTrue();

    List<Order> findAllByUser(User user);

    List<Order> findAllOrderByUserAndStatusOrderTrue(User user);

    @Query("SELECT o FROM Order o JOIN FETCH o.parking WHERE o.user = :user")
    List<Order> findAllByUserWithParking(@Param("user") User user);

}
