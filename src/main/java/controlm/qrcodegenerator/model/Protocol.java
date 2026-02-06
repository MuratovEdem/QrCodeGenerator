package controlm.qrcodegenerator.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "protocols")
@EqualsAndHashCode(of = {"id"})
public class Protocol {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "cipher", nullable = false, length = 10)
    private String cipher;

    @Column(name = "unique_number", nullable = false)
    private String uniqueNumber;

    @Column(name = "sequential_number", nullable = false)
    private String sequentialNumber;

    @Column(name = "issue_date") //TODO поставить обязательную дату
    private LocalDate issueDate;

    @Column(name = "file_path")
    private String filePath;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDate createdAt;

    @Transient
    public String getFullProtocolNumber() {
        return String.format("%s-%s-%s", cipher, uniqueNumber, sequentialNumber);
    }

    // TODO сделать кем создан протокол

    // TODO сделать пуличное и внутренне отображение протокола
}
