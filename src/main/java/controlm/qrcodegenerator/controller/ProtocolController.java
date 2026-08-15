package controlm.qrcodegenerator.controller;

import controlm.qrcodegenerator.dto.response.OcrJobResponseDto;
import controlm.qrcodegenerator.service.FileStorageService;
import controlm.qrcodegenerator.service.OcrJobService;
import controlm.qrcodegenerator.service.ProtocolService;
import controlm.qrcodegenerator.service.TempFileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.MalformedURLException;

@Controller
@RequestMapping("/protocols")
@RequiredArgsConstructor
public class ProtocolController {

    private final ProtocolService protocolService;
    private final TempFileStorageService tempFileStorageService;
    private final FileStorageService fileStorageService;
    private final OcrJobService ocrJobService;

    @GetMapping("/{id}/file")
    public ResponseEntity<Resource> downloadProtocol(@PathVariable Long id) throws MalformedURLException {
        Resource resource = protocolService.getProtocolFile(id);
        String fileName = protocolService.getProtocolFileName(id);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                .body(resource);
    }

    @GetMapping("/temp/{fileName}")
    public ResponseEntity<Resource> previewTempFile(@PathVariable String fileName) throws MalformedURLException {
        Resource file = tempFileStorageService.loadTempAsResource(fileName);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileName + "\"")
                .body(file);
    }

    @GetMapping("/original/{id}")
    public ResponseEntity<Resource> previewOriginalFile(@PathVariable Long id) throws MalformedURLException {
        OcrJobResponseDto dtoById = ocrJobService.getDtoById(id);
        Resource file = fileStorageService.loadAsResource(dtoById.getPath());

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }
}
