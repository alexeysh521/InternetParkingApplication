package com.example.RETURN.services;

import com.example.RETURN.dto.*;
import com.example.RETURN.enums.DepositDtoSlotBalance;
import com.example.RETURN.enums.OrderSlotStatus;
import com.example.RETURN.enums.ParkingSlotNumber;
import com.example.RETURN.enums.ParkingSlotSize;
import com.example.RETURN.models.Order;
import com.example.RETURN.models.User;
import com.example.RETURN.repositories.OrderRepository;
import com.example.RETURN.repositories.ParkingRepository;
import com.example.RETURN.repositories.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UserService {

    @Autowired private UserRepository userRepository;

    @Autowired private ModelMapper modelMapper;

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
    public AccountOperationDto forBalanceOperation(AccountOperationDto depositDto, User user){
        int amount = depositDto.getBalance();

        switch(DepositDtoSlotBalance.getOperationFromString(depositDto.getNameOperation())){
            case CHECK -> {
                return new AccountOperationDto("CHECK", user.getBalance(), user.getUserName());
            }
            case WITHDRAW -> {
                int newBalance = user.getBalance() - amount;
                if(newBalance < 0) throw new IllegalArgumentException("Недостаточно средств.");
                user.setBalance(newBalance);
                save(user);
                return new AccountOperationDto("WITHDRAW", newBalance, user.getUserName());
            }
            case DEPOSIT -> {
                int newBalance = user.getBalance() + amount;
                user.setBalance(newBalance);
                save(user);
                return new AccountOperationDto("DEPOSIT", newBalance, user.getUserName());
            }
            default ->
                throw new IllegalArgumentException("Неверный ввод.");
        }
    }


    public List<UserInfoDto> forAllUsers(){//
        List<User> users = userList();

        return users.stream()
                .map(this::convertToDto)
                .toList();
    }

    public UserInfoDto convertToDto(User user){
        return modelMapper.map(user, UserInfoDto.class);
    }

}
