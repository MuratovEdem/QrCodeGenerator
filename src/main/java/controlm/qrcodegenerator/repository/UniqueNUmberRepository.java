package controlm.qrcodegenerator.repository;

import controlm.qrcodegenerator.model.UniqueNumber;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UniqueNUmberRepository extends JpaRepository<UniqueNumber, Long> {
}
