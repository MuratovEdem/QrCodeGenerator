package controlm.qrcodegenerator.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ProtocolUpdateDto {
    @NotBlank(message = "Номер протокола обязателен")
    private String protocolNumber;

    @NotEmpty(message = "Дата обязательна")
    private String issueDate;

    private Long clientId;

    private MultipartFile file;
}
