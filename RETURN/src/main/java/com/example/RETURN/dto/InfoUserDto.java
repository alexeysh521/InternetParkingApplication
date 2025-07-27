package com.example.RETURN.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class InfoUserDto {

    private int id;

    private String userName;

    private String email;

    private String role;

    private int balance;

    private List<CarDto> cars = new ArrayList<>();

    private List<InfoOrderDto> orders = new ArrayList<>();

}
