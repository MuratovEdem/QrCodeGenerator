package controlm.qrcodegenerator.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ocr_protocol_preview")
@Getter
@Setter
public class OcrProtocolPreview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long ocrJobId;
    private String fileName;
    private String protocolNumber;
    private String issueDate;
}
