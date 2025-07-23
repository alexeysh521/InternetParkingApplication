package com.example.RETURN.services;

import com.example.RETURN.dto.*;
import com.example.RETURN.enums.DepositDtoSlotBalance;
import com.example.RETURN.models.User;
import com.example.RETURN.repositories.UserRepository;
import com.example.RETURN.services.impl.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final ModelMapper modelMapper;

    public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public void save(User user){
        userRepository.save(user);
    }

    public boolean existsByUserName(String userName){
        return userRepository.existsByUserName(userName);
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
