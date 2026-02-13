package controlm.qrcodegenerator.repository;

import controlm.qrcodegenerator.enums.OcrJobStatus;
import controlm.qrcodegenerator.model.OcrJob;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OcrJobRepository extends JpaRepository<OcrJob, Long> {
    List<OcrJob> findAllByUserId(Long userId);
    List<OcrJob> findAllByUserIdAndStatusNot(Long userId, OcrJobStatus status);
}
