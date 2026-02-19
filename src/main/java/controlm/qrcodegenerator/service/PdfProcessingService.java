package controlm.qrcodegenerator.service;

import controlm.qrcodegenerator.dto.response.ProtocolPreviewDto;
import controlm.qrcodegenerator.mapper.ProtocolMapper;
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

    public List<ProtocolPreviewDto> analyze(File pdfFile, IntConsumer progressCallback, Integer protocolSize) throws Exception {

        List<ProtocolPreviewDto> previews = new ArrayList<>();

        try (RandomAccessReadBufferedFile rar = new RandomAccessReadBufferedFile(pdfFile);
             PDDocument source = Loader.loadPDF(rar)) {

            PDFRenderer renderer = new PDFRenderer(source);

            PDDocument currentProtocol = null;
            ProtocolMetadata currentMeta = null;

            int pageIndex = 0;
            int pageCounter = 0;

            for (PDPage page : source.getPages()) {

                try {
                    boolean isFirstPageOfProtocol =
                            currentProtocol == null || pageCounter == 0;

                    ProtocolMetadata foundMeta = null;

                    if (isFirstPageOfProtocol || protocolSize <= 0) {
                        foundMeta = findProtocolStart(renderer, pageIndex);
                    }

                    if (foundMeta != null) {

                        if (currentProtocol != null) {
                            savePreview(currentProtocol, currentMeta, previews);
                            currentProtocol.close();
                        }

                        currentProtocol = new PDDocument();
                        currentMeta = foundMeta;
                        pageCounter = 0;
                    }

                    if (currentProtocol != null) {
                        currentProtocol.importPage(page);
                        pageCounter++;
                    }

                    if (protocolSize > 0 && pageCounter >= protocolSize) {
                        savePreview(currentProtocol, currentMeta, previews);
                        currentProtocol.close();

                        currentProtocol = null;
                        currentMeta = null;
                        pageCounter = 0;
                    }

                    int progress = (pageIndex + 1) * 100 / source.getNumberOfPages();
                    progressCallback.accept(progress);

                    pageIndex++;
                } catch (Exception e) {
                    throw new IllegalArgumentException("Страница повреждена: " + pageIndex, e);
                }
            }

            if (currentProtocol != null) {
                savePreview(currentProtocol, currentMeta, previews);
                currentProtocol.close();
            }
        }

        return previews;
    }

    @Transactional
    public void confirm(List<ProtocolPreviewDto> approved, Long clientId) throws Exception {
        for (ProtocolPreviewDto dto : approved) {

            if (!protocolService.existByCipherAndUniqueNumberAndSequenceNumber(dto, clientId)) {
                File temp = tempStorage.get(dto.getFileName());
                Path finalPath = finalStorage.moveToFinalStorage(temp, dto, clientId);

                protocolService.createProtocolFromPdf(
                        clientId,
                        dto.getNumber(),
                        dto.getIssueDate(),
                        finalPath.toString()
                );

                tempStorage.delete(dto.getFileName());
                ocrProtocolPreviewService.deleteByFileName(dto.getFileName());
            }
        }

    }

    private ProtocolMetadata findProtocolStart(PDFRenderer renderer, int index) throws Exception {

        BufferedImage img = renderer.renderImageWithDPI(index, 200);

        String text = fastOcrService.recognizeHeader(img);
        return recognizer.extract(text);
    }

    private void savePreview(PDDocument currentProtocol,
                             ProtocolMetadata meta,
                             List<ProtocolPreviewDto> previews) throws IOException {

        String tempId = tempStorage.saveTemp(currentProtocol);

        previews.add(new ProtocolPreviewDto(
                tempId,
                meta.number(),
                meta.issueDate()
        ));
    }
}
