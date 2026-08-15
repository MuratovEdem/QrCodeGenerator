package controlm.qrcodegenerator.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegistrationUserResponseDto {
    private String username;
    private String password;
}
