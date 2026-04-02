package controlm.qrcodegenerator.repository;

import controlm.qrcodegenerator.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContactRepository extends JpaRepository<Contact, Long> {
    List<Contact> findAllByClientId(Long clientId);
}
