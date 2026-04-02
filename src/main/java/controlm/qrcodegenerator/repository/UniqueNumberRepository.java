package controlm.qrcodegenerator.repository;

import controlm.qrcodegenerator.model.UniqueNumber;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UniqueNumberRepository extends JpaRepository<UniqueNumber, Long> {

    @Query("SELECT MAX(u.number) FROM UniqueNumber u")
    Long findMaxNumber();
    boolean existsByNumber(Long number);
}
