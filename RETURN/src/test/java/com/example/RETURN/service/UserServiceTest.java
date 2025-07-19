package com.example.RETURN.service;

import com.example.RETURN.models.User;
import com.example.RETURN.repositories.UserRepository;
import com.example.RETURN.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void save_ifItPersistsUser_theUserSave() {
        User user = new User("Alex", "ADMIN", "alex@mail.ru", "1234");
        userService.save(user);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void userList_ifItPersistsUser_theUserListReturn(){
        List<User> userList = List.of(
                new User("Alex", "ADMIN", "alex@mail.ru", "1234"),
                new User("Max", "USER", "max@mail.ru", "1234")
        );

        when(userRepository.findAll()).thenReturn(userList);

        List<User> result = userService.userList();

        assertEquals(userList, result);

        verify(userRepository, times(1)).findAll();
    }


}