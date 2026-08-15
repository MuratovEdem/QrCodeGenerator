package controlm.qrcodegenerator.dto.request;

import lombok.Data;

@Data
public class RegistrationUserRequestDto {

    private String name;
    private String surname;
    private String patronymic;
    private String role;
}
