package com.example.RETURN.controllers;

import com.example.RETURN.dto.AuthRequest;
import com.example.RETURN.dto.RegisterRequest;
import com.example.RETURN.models.User;
import com.example.RETURN.services.JwtServiceImpl;
import com.example.RETURN.services.UserServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired private AuthenticationManager authenticationManager;
    @Autowired private JwtServiceImpl jwtService;
    @Autowired private UserDetailsService userDetailsService;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private UserServiceImpl userService;

    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody AuthRequest request) {//войти в свой аккаунт

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUserName(), request.getPassword())
        );

        UserDetails user = userDetailsService.loadUserByUsername(request.getUserName());
        String token = jwtService.generateToken(user);

        return ResponseEntity.ok("Bearer " + token);
    }


    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest request) {//зарегистрироваться

        if(userService.existsByUserName(request.getUserName())){
            return ResponseEntity.badRequest().body("Пользователь с этим именем уже существует");
        }

        User user = new User(
                request.getUserName(),
                request.getRole(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword())
        );

        userService.save(user);

        UserDetails userd = userDetailsService.loadUserByUsername(request.getUserName());
        String token = jwtService.generateToken(userd);
        return ResponseEntity.ok("Bearer " + token);
    }



}


