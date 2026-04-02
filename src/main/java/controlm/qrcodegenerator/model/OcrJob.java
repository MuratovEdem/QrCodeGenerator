package controlm.qrcodegenerator.model;

import controlm.qrcodegenerator.enums.OcrJobStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "ocr_job")
@Getter
@Setter
public class OcrJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "client_id")
    private Long clientId;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "original_file_path")
    private String originalFilePath;

    @Enumerated(EnumType.STRING)
    private OcrJobStatus status;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "finished_at")
    private LocalDateTime finishedAt;
}
