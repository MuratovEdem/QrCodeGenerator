package controlm.qrcodegenerator.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProtocolRequestDto {

    @NotBlank(message = "Шифр протокола обязателен")
    @Size(max = 10, message = "Шифр не должен превышать 10 символов")
    private String cipher;

    @NotBlank(message = "Номер заказчика обязателен")
    private String uniqueNumber;

    @NotBlank(message = "Порядковый номер обязателен")
    private String sequentialNumber;

    private Long clientId;
}
