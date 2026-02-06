package controlm.qrcodegenerator.service;

import controlm.qrcodegenerator.dto.response.ProtocolPreviewDto;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class PdfProcessingService {

    private final FastOcrService fastOcrService;
    private final ProtocolRecognizer recognizer;
    private final ProtocolService protocolService;
    private final TempFileStorageService tempStorage;
    private final FinalFileStorageService finalStorage;

//    public List<ProtocolPreviewDto> analyze(File pdfFile, int protocolSize) throws Exception {
//        List<ProtocolPreviewDto> previews = new ArrayList<>();
//
//        try (RandomAccessReadBufferedFile rar = new RandomAccessReadBufferedFile(pdfFile)) {
//            PDDocument source = Loader.loadPDF(rar);
//
//            PDFRenderer renderer = new PDFRenderer(source);
//            ProtocolMetadata meta = null;
//
//            int indexPage = 0;
//            int counter = 1;
//            PDDocument protocolDoc = new PDDocument();
//            for (PDPage page : source.getPages()) {
//                try {
//                    if (counter == 1) {
//                        meta = findProtocolStart(renderer, indexPage);
//                        protocolDoc = new PDDocument();
//                    }
//
//                    protocolDoc.importPage(page);
//
//                    if (meta == null) {
//                        indexPage++;
//                        continue;
//                    }
//
//                    if (counter >= protocolSize) {
//                        String tempId = tempStorage.saveTemp(protocolDoc);
//                        protocolDoc.close();
//
//                        previews.add(new ProtocolPreviewDto(
//                                tempId,
//                                meta.number(),
//                                meta.issueDate()
//                        ));
//                        counter = 0;
//                    }
//                    counter++;
//                    indexPage++;
//
//                } catch (Exception e) {
//                    throw new IllegalArgumentException("Страница повреждена");
//                }
//            }
//
//        }
//
//        return previews;
//    }

    public List<ProtocolPreviewDto> analyze(File pdfFile, Integer protocolSize) throws Exception {

        // TODO сделать добавление в протокол страницы идущей
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

                        currentProtocol = null;
                        currentMeta = null;
                        pageCounter = 0;
                    }

                    pageIndex++;

                } catch (Exception e) {
                    throw new IllegalArgumentException("Страница повреждена: " + pageIndex, e);
                }
            }

            if (currentProtocol != null) {
                savePreview(currentProtocol, currentMeta, previews);
            }
        }

        return previews;
    }

    @Transactional
    public void confirm(List<ProtocolPreviewDto> approved, Long clientId) throws Exception {

        for (ProtocolPreviewDto dto : approved) {

            File temp = tempStorage.get(dto.getFileName());
            Path finalPath = finalStorage.moveToFinalStorage(temp, dto, clientId);

            protocolService.createProtocolFromPdf(
                    clientId,
                    dto.getNumber(),
                    dto.getIssueDate(),
                    finalPath.toString()
            );

            tempStorage.delete(dto.getFileName());
        } // TODO сделать возврат дубликатов и вопрос о перезаписи
    }

    private ProtocolMetadata findProtocolStart(PDFRenderer renderer, int index) throws Exception {

        BufferedImage img = renderer.renderImageWithDPI(index, 150);
        String text = fastOcrService.recognizeHeader(img);
        log.info(text);
        return recognizer.extract(text);
    }

    private boolean looksLikeProtocolStart(String text) {
        if (text == null || text.isBlank()) return false;

        String normalized = text
                .toLowerCase()
                .replaceAll("\\s", " ");

        return normalized.contains("инн") && normalized.contains("кпп");
    }

    private void savePreview(PDDocument currentProtocol,
                             ProtocolMetadata meta,
                             List<ProtocolPreviewDto> previews) throws IOException {

        String tempId = tempStorage.saveTemp(currentProtocol);
        currentProtocol.close();

        previews.add(new ProtocolPreviewDto(
                tempId,
                meta.number(),
                meta.issueDate()
        ));
    }
}
