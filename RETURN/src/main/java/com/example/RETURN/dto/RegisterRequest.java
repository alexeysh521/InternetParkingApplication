package com.example.RETURN.dto;

import com.example.RETURN.annotation.UniqueEmail;
import com.example.RETURN.annotation.UniqueUserName;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @UniqueUserName
    @NotBlank(message = "Имя не должно быть пустым")
    @Size(min = 2, max = 20, message = "Имя меньше 2 или больше 20 символов")
    private String userName;

    @NotBlank(message = "пароль не должен быть пустым")
    @Size(min = 2, max = 20, message = "пароль меньше 4 или больше 25 символов")
    private String password;

    @UniqueEmail
    @NotBlank(message = "Email не должен быть пустым")
    @Email(message = "Неверный формат email")
    private String email;

    @NotBlank(message = "Роль обязательна")
    private String role; // ROLE_USER, ROLE_ADMIN, ROLE_SECURITY


}
