package controlm.qrcodegenerator.dto.response;

import lombok.Data;

@Data
public class FailedFileAdminDto {
    private Long id;
    private Long clientId;
    private String clientName;
    private String fileName;
    private String contentType;
}
