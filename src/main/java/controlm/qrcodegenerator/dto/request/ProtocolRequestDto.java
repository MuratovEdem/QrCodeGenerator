package controlm.qrcodegenerator.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Data
public class ProtocolRequestDto {

    @NotBlank(message = "Шифр протокола обязателен")
    @Size(max = 10, message = "Шифр не должен превышать 10 символов")
    private String cipher;

    @NotBlank(message = "Номер заказчика обязателен")
    private String uniqueNumber;

    @NotBlank(message = "Порядковый номер обязателен")
    private String sequentialNumber;

    @NotEmpty(message = "Дата обязательна")
    private LocalDate issueDate;

    private Long clientId;

    @NotEmpty(message = "Файл обязателен")
    private MultipartFile file;

    public String getFullNumber() {
        return String.format("%s-%s-%s", cipher, uniqueNumber, sequentialNumber);;
    }
}
