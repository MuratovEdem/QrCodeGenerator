package controlm.qrcodegenerator.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ContractRequestDto {

    @NotBlank(message = "Номер договора обязателен")
    private String name;
}
