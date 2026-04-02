package controlm.qrcodegenerator.service;

import controlm.qrcodegenerator.dto.response.OcrJobEvent;
import controlm.qrcodegenerator.dto.response.ProtocolPreviewDto;
import controlm.qrcodegenerator.enums.OcrJobStatus;
import controlm.qrcodegenerator.model.OcrJob;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OcrAsyncService {

    private final PdfProcessingService pdfProcessingService;
    private final OcrJobService ocrJobService;
    private final OcrWsNotifier wsNotifier;
    private final OcrProtocolPreviewService ocrProtocolPreviewService;

    @Async("ocrExecutor")
    public void start(Long ocrJobId) {
        OcrJob job = ocrJobService.findById(ocrJobId);

        try {
            job.setStatus(OcrJobStatus.PROCESSING);
            ocrJobService.save(job);

            wsNotifier.sendToUser(job.getCreatedBy(), new OcrJobEvent(ocrJobId, OcrJobStatus.PROCESSING.getName(), "Стар OCR", 0));

            File pdf = new File(job.getOriginalFilePath());

            List<ProtocolPreviewDto> previews = pdfProcessingService.analyze(pdf,
                    progress -> wsNotifier.sendToUser(job.getCreatedBy(), new  OcrJobEvent(ocrJobId, OcrJobStatus.PROCESSING.getName(), null, progress)),
                    0);
            // TODO СДЕЛАТЬ ОБРАБОТКУ РЕЗУЛЬТАТА Добавить protocolSize

            ocrProtocolPreviewService.save(ocrJobId, previews);

            job.setStatus(OcrJobStatus.DONE);
            job.setFinishedAt(LocalDateTime.now());
            ocrJobService.save(job);

            wsNotifier.sendToUser(job.getCreatedBy(), new OcrJobEvent(ocrJobId, OcrJobStatus.DONE.getName(), "Готово", 100));

        } catch (Exception e) {
            job.setStatus(OcrJobStatus.ERROR);

            job.setErrorMessage(e.getMessage());
            ocrJobService.save(job);

            wsNotifier.sendToUser(job.getCreatedBy(), new OcrJobEvent(ocrJobId, OcrJobStatus.ERROR.getName(), e.getMessage(), null));
        }
    }
}
