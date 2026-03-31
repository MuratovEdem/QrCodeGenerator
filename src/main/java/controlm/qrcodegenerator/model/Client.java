package controlm.qrcodegenerator.model;

import jakarta.persistence.*;
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

    @OneToMany(mappedBy = "client", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Contact> contacts;

    @OneToMany(mappedBy = "client")
    private List<Protocol> protocols;

    @OneToMany(mappedBy = "client", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<ConstructionSite> constructionSites;

    @OneToMany(mappedBy = "client", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<Contract> contracts;

    @OneToMany(mappedBy = "client", orphanRemoval = true, cascade = CascadeType.ALL)
    private List<UniqueNumber> uniqueNumbers;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClientFile> clientFiles;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

//    TODO кем создан
    // TODO кем и когда отредактирован
}
