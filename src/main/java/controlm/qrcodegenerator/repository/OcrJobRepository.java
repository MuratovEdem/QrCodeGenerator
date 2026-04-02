package controlm.qrcodegenerator.repository;

import controlm.qrcodegenerator.enums.OcrJobStatus;
import controlm.qrcodegenerator.model.OcrJob;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OcrJobRepository extends JpaRepository<OcrJob, Long> {
    List<OcrJob> findAllByUserId(Long userId);
    Page<OcrJob> findByUserIdAndStatusNot(Long userId, OcrJobStatus status, Pageable pageable);
    Long countByUserIdAndStatusNotIn(Long userId, List<OcrJobStatus> excludedStatuses);
    Long countByUserIdAndStatus(Long userId, OcrJobStatus ocrJobStatus);
}

