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

    public List<ProtocolPreviewDto> analyze(File pdfFile, int protocolSize) throws Exception {
        List<ProtocolPreviewDto> previews = new ArrayList<>();

        try (RandomAccessReadBufferedFile rar = new RandomAccessReadBufferedFile(pdfFile)) {
            PDDocument source = Loader.loadPDF(rar);

            PDFRenderer renderer = new PDFRenderer(source);
            ProtocolMetadata meta = null;

            int index = 0;
            int counter = 1;
            PDDocument protocolDoc = null;
            for (PDPage page : source.getPages()) {
                try {
                    if (counter == 1) {
                        meta = findProtocolStart(renderer, index);
                        protocolDoc = new PDDocument();
                    }

                    protocolDoc.importPage(page);

                    if (meta == null) {
                        index++;
                        continue;
                    }

                    if (counter >= protocolSize) {
                        String tempId = tempStorage.saveTemp(protocolDoc);
                        protocolDoc.close();

                        previews.add(new ProtocolPreviewDto(
                                tempId,
                                meta.number(),
                                meta.issueDate()
                        ));
                        counter = 0;
                    }
                    counter++;
                    index++;

                } catch (Exception e) {
                    throw new IllegalArgumentException("Страница повреждена");
                }
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
        ProtocolMetadata meta = recognizer.extract(text);
        if (meta != null) return meta;

        for (int k = 1; k <= 2; k++) {
            try {
                img = renderer.renderImageWithDPI(index + k, 150);
                text = fastOcrService.recognizeHeader(img);
                meta = recognizer.extract(text);
                if (meta != null) return meta;
            } catch (Exception ignored) {}
        }

        return null;
    }
}
