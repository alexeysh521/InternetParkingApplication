package com.example.RETURN.models;

import com.example.RETURN.enums.ParkingSlotNumber;
import com.example.RETURN.enums.ParkingSlotSize;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "parkingSpace")
public class ParkingSpace {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "number")
    private ParkingSlotNumber parkingSlotNumber; //номер парковки

    @Enumerated(EnumType.STRING)
    @Column(name = "size")
    private ParkingSlotSize parkingSlotSize;//размеры парковочного места

    private Boolean status = true;//свободно ли место?

    @OneToMany(mappedBy = "parking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders;

    public ParkingSpace() {}

    public ParkingSpace(ParkingSlotNumber number, ParkingSlotSize size) {
        this.parkingSlotNumber = number;
        this.parkingSlotSize = size;
    }

    public ParkingSlotNumber getParkingSlotNumber() {
        return parkingSlotNumber;
    }

    public void setParkingSlotNumber(ParkingSlotNumber number) {
        this.parkingSlotNumber = number;
    }

    public ParkingSlotSize getParkingSlotSize() {
        return parkingSlotSize;
    }

    public void setParkingSlotSize(ParkingSlotSize parkingSlotSize) {
        this.parkingSlotSize = parkingSlotSize;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
