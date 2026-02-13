package controlm.qrcodegenerator.service;

import controlm.qrcodegenerator.dto.response.ProtocolPreviewDto;
import controlm.qrcodegenerator.model.OcrProtocolPreview;
import controlm.qrcodegenerator.repository.OcrProtocolPreviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OcrProtocolPreviewService {

    private final OcrProtocolPreviewRepository ocrProtocolPreviewRepository;
    private final TempFileStorageService tempFileStorageService;

    public void save(Long ocrJobId, List<ProtocolPreviewDto> previews) {
        for (ProtocolPreviewDto protocolPreviewDto : previews) {
            ocrProtocolPreviewRepository.save(create(protocolPreviewDto, ocrJobId));
        }
    }

    public List<OcrProtocolPreview> findAllByOcrJobId(Long ocrJobId) {
        return ocrProtocolPreviewRepository.findAllByOcrJobId(ocrJobId);
    }

    @Transactional
    public void deleteAllByOcrJobId(Long ocrJobId) throws IOException {
        tempFileStorageService.deleteTempFilesFromOcrProtocolPreview(findAllByOcrJobId(ocrJobId));
        ocrProtocolPreviewRepository.deleteAllByOcrJobId(ocrJobId);
    }

    private OcrProtocolPreview create(ProtocolPreviewDto previewDto, Long ocrJobId) {
        OcrProtocolPreview ocrProtocolPreview = new OcrProtocolPreview();

        ocrProtocolPreview.setProtocolNumber(previewDto.getNumber());
        ocrProtocolPreview.setIssueDate(previewDto.getIssueDate());
        ocrProtocolPreview.setFileName(previewDto.getFileName());
        ocrProtocolPreview.setOcrJobId(ocrJobId);

        return ocrProtocolPreview;
    }
}
