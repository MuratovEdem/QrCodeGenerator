package controlm.qrcodegenerator.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

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

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private List<Contact> contacts;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private List<Protocol> protocols;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private List<ConstructionSite> constructionSites;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private List<Contract> contracts;

    @OneToMany(mappedBy = "client")
    private List<UniqueNumber> uniqueNumbers;

    // TODO добавить поля договора, обьекты, инн, кпп, контактные лица
}
