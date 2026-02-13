package controlm.qrcodegenerator.repository;

import controlm.qrcodegenerator.model.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    Page<Client> findByNameIsContainingIgnoreCase(String name, Pageable pageable);
}
