package com.example.RETURN.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;//id заказа

    private long price;//стоимость заказа

    private boolean statusOrder = true;

    @FutureOrPresent
    private LocalDateTime startTime;//начало парковки

    @Future
    private LocalDateTime endTime;//конец парковки

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;//получим все данные про пользователя, и машину, которую он хочет припарковать

    @ManyToOne
    @JoinColumn(name = "parking_id")
    private ParkingSpace parking;//данные о парковочном месте

    public Order(User user, ParkingSpace parking, long price, LocalDateTime startTime,
                 LocalDateTime endTime) {
        this.user = user;
        this.parking = parking;
        this.price = price;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Order() {}

    public boolean isStatusOrder() {
        return statusOrder;
    }

    public void setStatusOrder(boolean status_order) {
        this.statusOrder = status_order;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public long getId() {
        return id;
    }

    public void setId(long id){
        this.id = id;
    }

    public ParkingSpace getParking() {
        return parking;
    }

    public void setParking(ParkingSpace parking) {
        this.parking = parking;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "Order{" +
                "endTime=" + endTime +
                ", id=" + id +
                ", price=" + price +
                ", statusOrder=" + statusOrder +
                ", startTime=" + startTime +
                ", user=" + user +
                ", parking=" + parking +
                '}';
    }
}
