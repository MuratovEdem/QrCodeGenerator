package controlm.qrcodegenerator.dto.response;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AuditLogDto {
    private String action;
    private String targetUsername;
    private String details;
    private String createdBy;
    private LocalDateTime createdAt;
}
