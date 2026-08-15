package controlm.qrcodegenerator.dto.response;

import controlm.qrcodegenerator.enums.OcrJobStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OcrJobResponseDto {

    private Long id;
    private Long clientId;
    private String clientName;

    private String createdBy;
    private Integer progress;

    private Long userId;

    private String originalFileName;
    private String path;

    private OcrJobStatus status;

    private String errorMessage;

    private LocalDateTime createdAt;
    private LocalDateTime finishedAt;
}
