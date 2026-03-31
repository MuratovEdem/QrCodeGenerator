package controlm.qrcodegenerator.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "client_files")
@Data
public class ClientFile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;

    private String filePath;

    private String contentType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;
}
