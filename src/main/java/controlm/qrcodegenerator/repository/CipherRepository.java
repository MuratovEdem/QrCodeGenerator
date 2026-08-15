package controlm.qrcodegenerator.repository;

import controlm.qrcodegenerator.model.Cipher;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CipherRepository extends JpaRepository<Cipher, Long> {
}
