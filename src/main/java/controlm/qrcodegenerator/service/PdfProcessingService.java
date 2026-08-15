package controlm.qrcodegenerator.service;

import controlm.qrcodegenerator.dto.response.ProtocolPreviewDto;
import controlm.qrcodegenerator.mapper.ProtocolMapper;
import controlm.qrcodegenerator.model.Protocol;
import controlm.qrcodegenerator.model.ProtocolMetadata;
import controlm.qrcodegenerator.utils.ProtocolRecognizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBufferedFile;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdfProcessingService {

    private final FastOcrService fastOcrService;
    private final ProtocolRecognizer recognizer;
    private final ProtocolService protocolService;
    private final TempFileStorageService tempStorage;
    private final FileStorageService finalStorage;
    private final OcrProtocolPreviewService ocrProtocolPreviewService;
    private final ClientService clientService;
    private final ProtocolMapper protocolMapper;

    public List<ProtocolPreviewDto> analyze(File pdfFile, IntConsumer progressCallback) throws Exception {
        List<ProtocolPreviewDto> previews = new ArrayList<>();

        try (RandomAccessReadBufferedFile rar = new RandomAccessReadBufferedFile(pdfFile);
             PDDocument source = Loader.loadPDF(rar)) {

            PDFRenderer renderer = new PDFRenderer(source);
            int totalPages = source.getNumberOfPages();

            PDDocument currentDoc = null;
            ProtocolMetadata currentMeta = null;

            for (int pageIndex = 0; pageIndex < totalPages; pageIndex++) {
                try {
                    PDPage page = source.getPage(pageIndex);

                    // Пытаемся найти начало протокола на каждой странице
                    ProtocolMetadata foundMeta = findProtocolStart(renderer, pageIndex);

                    if (foundMeta != null) {
                        // Найден новый протокол — закрываем предыдущий, если был
                        if (currentDoc != null) {
                            saveAndClosePreview(currentDoc, currentMeta, previews);
                        }
                        currentDoc = new PDDocument();
                        currentMeta = foundMeta;
                    }

                    // Если есть активный протокол, добавляем в него страницу
                    if (currentDoc != null) {
                        currentDoc.importPage(page);
                    } else {
                        log.warn("Page {} has no protocol context and no metadata found — skipped", pageIndex + 1);
                    }

                    int progress = (pageIndex + 1) * 100 / totalPages;
                    progressCallback.accept(progress);

                } catch (Exception e) {
                    log.error("Error processing page {}: {}", pageIndex + 1, e.getMessage(), e);
                    // При ошибке закрываем текущий протокол (если есть) и сбрасываем состояние
                    if (currentDoc != null) {
                        try {
                            saveAndClosePreview(currentDoc, currentMeta, previews);
                        } catch (Exception ex) {
                            log.error("Failed to save document after page error", ex);
                        }
                        currentDoc = null;
                        currentMeta = null;
                    }
                }
            }

            // Сохраняем последний протокол
            if (currentDoc != null) {
                saveAndClosePreview(currentDoc, currentMeta, previews);
            }
        }

        return previews;
    }

    @Transactional
    public void confirm(List<ProtocolPreviewDto> approved, Long clientId) throws Exception {
        for (ProtocolPreviewDto dto : approved) {
            if (!protocolService.existByProtocolNumberAndClientId(dto, clientId)) {
                File temp = tempStorage.get(dto.getFileName());
                Path finalPath = finalStorage.moveToFinalStorage(temp, dto, clientService.getNameById(clientId));

                protocolService.createProtocolFromPdf(
                        clientId,
                        dto.getNumber(),
                        dto.getIssueDate(),
                        finalPath.toString()
                );

                tempStorage.delete(dto.getFileName());
                ocrProtocolPreviewService.deleteByFileName(dto.getFileName());
            } else {
                File temp = tempStorage.get(dto.getFileName());
                Path finalPath = finalStorage.moveToFinalStorage(temp, dto, clientService.getNameById(clientId));

                Protocol protocol = protocolService.getByNumber(dto.getNumber());
                protocol.setIssueDate(protocolMapper.parseDate(dto.getIssueDate()));
                protocol.setFilePath(finalPath.toString());

                tempStorage.delete(dto.getFileName());
                ocrProtocolPreviewService.deleteByFileName(dto.getFileName());
            }
        }

    }

    private ProtocolMetadata findProtocolStart(PDFRenderer renderer, int index) throws Exception {

        BufferedImage img = renderer.renderImageWithDPI(index, 150);

        String text = fastOcrService.recognizeHeader(img);
        return recognizer.extract(text);
    }

    private void saveAndClosePreview(PDDocument currentProtocol,
                                     ProtocolMetadata meta,
                                     List<ProtocolPreviewDto> previews) throws IOException {

        try (currentProtocol) {
            String tempId = tempStorage.saveTemp(currentProtocol);

            previews.add(new ProtocolPreviewDto(
                    tempId,
                    meta.number(),
                    meta.issueDate()
            ));
        }
    }
}
