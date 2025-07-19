package com.example.RETURN.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;



@Data
public class AuthRequest {

    @NotBlank(message = "Имя не должно быть пустым")
    @Size(min = 2, max = 20, message = "Имя меньше 2 или больше 20 символов")
    private String userName;

    @NotBlank(message = "пароль не должен быть пустым")
    @Size(min = 2, max = 20, message = "пароль меньше 4 или больше 25 символов")
    private String password;

}
