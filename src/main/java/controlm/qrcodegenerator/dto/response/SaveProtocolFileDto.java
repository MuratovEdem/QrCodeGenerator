package controlm.qrcodegenerator.dto.response;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class SaveProtocolFileDto {
    private String fullNumber;
    private String issueDate;
    private MultipartFile file;
    private String clientName;
}
