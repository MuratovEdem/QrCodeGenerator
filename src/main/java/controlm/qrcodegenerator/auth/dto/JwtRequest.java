package controlm.qrcodegenerator.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class JwtRequest {
    @NotBlank(message = "Логин не может быть пустым")
    @Size(min = 2, max = 50, message = "Логин должен быть от 2 до 50 символов")
    private String username;

    @NotBlank(message = "Пароль не может быть пустым")
    private String password;
}
