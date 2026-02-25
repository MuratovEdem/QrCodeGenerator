package controlm.qrcodegenerator.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "clients")
@EqualsAndHashCode(of = {"id"})
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String innKpp;

    @OneToMany(mappedBy = "client")
    private List<Contact> contacts;

    @OneToMany(mappedBy = "client")
    private List<Protocol> protocols;

    @OneToMany(mappedBy = "client")
    private List<ConstructionSite> constructionSites;

    @OneToMany(mappedBy = "client")
    private List<Contract> contracts;

    @OneToMany(mappedBy = "client")
    private List<UniqueNumber> uniqueNumbers;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

//    TODO кем создан
    // TODO кем и когда отредактирован
}
