package controlm.qrcodegenerator.dto.response;

import lombok.Data;

@Data
public class FailedFileDto {
    private Long id;
    private String fileName;
    private String contentType;
}
