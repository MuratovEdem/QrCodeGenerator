package controlm.qrcodegenerator.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ContactRequestDto {

    private Long id;
    @NotBlank(message = "Имя обязательно")
    private String name;
    private String post;
    @NotBlank(message = "Номер обязателен")
    private String phoneNumber;
    private String email;
}
