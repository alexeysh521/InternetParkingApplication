package com.example.RETURN.repositories;

import com.example.RETURN.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    boolean existsByEmail(String email);

    boolean existsByUserName(String userName);

    Optional<User> findByUserName(String userName);

    Optional<User> findUserById(int id);

    boolean existsOrderByUserName(String name);

}
