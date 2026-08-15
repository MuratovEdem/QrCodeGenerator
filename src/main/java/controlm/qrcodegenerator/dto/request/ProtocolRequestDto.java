package controlm.qrcodegenerator.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ProtocolRequestDto {

    @NotBlank(message = "Номер протокола обязателен")
    private String protocolNumber;

    @NotEmpty(message = "Дата обязательна")
    private String issueDate;

    private Long clientId;

    @NotNull
    private MultipartFile file;

    public String getFullNumber() {
        return String.format("%s", protocolNumber);
    }
}
