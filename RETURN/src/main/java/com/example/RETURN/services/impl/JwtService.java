package com.example.RETURN.services.impl;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface JwtService {

    String generateToken(UserDetails userDetails);

    List<String> extractRoles(String token);

    boolean isTokenValid(String token, UserDetails userDetails);

    String extractUsername(String token);

}
