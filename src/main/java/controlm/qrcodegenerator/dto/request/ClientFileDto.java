package controlm.qrcodegenerator.dto.request;

import lombok.Data;

@Data
public class ClientFileDto {
    private Long id;
    private String fileName;
    private String contentType;
    private String filePath;
}
