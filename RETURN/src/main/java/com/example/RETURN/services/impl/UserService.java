package com.example.RETURN.services.impl;

import com.example.RETURN.dto.AccountOperationDto;
import com.example.RETURN.dto.UserInfoDto;
import com.example.RETURN.models.User;

import java.util.List;

public interface UserService {

    void save(User user);

    boolean existsByUserName(String userName);

    List<User> userList();

    AccountOperationDto forBalanceOperation(AccountOperationDto depositDto, User user);

    List<UserInfoDto> forAllUsers();

    UserInfoDto convertToDto(User user);
}
