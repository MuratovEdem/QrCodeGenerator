package controlm.qrcodegenerator.repository;

import controlm.qrcodegenerator.model.OcrProtocolPreview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OcrProtocolPreviewRepository extends JpaRepository<OcrProtocolPreview, Long> {

    List<OcrProtocolPreview> findAllByOcrJobId(Long ocrJobId);
    void deleteAllByOcrJobId(Long ocrJobId);

    void deleteByFileName(String fileName);
}
