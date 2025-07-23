package com.example.RETURN.services;

import com.example.RETURN.models.User;
import com.example.RETURN.services.impl.OrderAndUserUtilsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class OrderAndUserUtilsServiceImpl implements OrderAndUserUtilsService {

    public boolean balanceUser(User user, int value){
        return user.getBalance() >= value;
    }

}

