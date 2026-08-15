package controlm.qrcodegenerator.repository;

import controlm.qrcodegenerator.model.FailedFile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FailedFileRepository extends JpaRepository<FailedFile, Long> {

    List<FailedFile> findByClientId(Long clientId);
}
