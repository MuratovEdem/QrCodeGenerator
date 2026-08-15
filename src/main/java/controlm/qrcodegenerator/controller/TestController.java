package controlm.qrcodegenerator.controller;

import controlm.qrcodegenerator.model.OcrJob;
import controlm.qrcodegenerator.service.ClientService;
import controlm.qrcodegenerator.service.FileStorageService;
import controlm.qrcodegenerator.service.OcrAsyncService;
import controlm.qrcodegenerator.service.OcrJobService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
@Controller
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestController {

    private final FileStorageService fileStorageService;
    private final OcrJobService ocrJobService;
    private final OcrAsyncService ocrAsyncService;
    private final ClientService clientService;

    @GetMapping("/pdfs")
    public String testPdfs() {
        Long clientId = 1L;
        String name = "admin_a";
        Path pdfsDir = Paths.get("./pdfs");

        if (!Files.exists(pdfsDir)) {
            log.warn("Directory ./pdfs does not exist");
            return "redirect:/clients";
        }

        try (Stream<Path> paths = Files.walk(pdfsDir)) {
            List<Path> pdfFiles = paths
                    .filter(Files::isRegularFile)
                    .filter(p -> p.toString().toLowerCase().endsWith(".pdf"))
                    .toList();

            log.info("Found {} PDF files", pdfFiles.size());

            for (int i = 0; i < pdfFiles.size(); i++) {
                Path pdfPath = pdfFiles.get(i);
                log.info("[{}/{}] Start processing: {}", i + 1, pdfFiles.size(), pdfPath);

                MultipartFile multipartFile = new PathMultipartFile(pdfPath);
                log.info("[{}/{}] MultipartFile created, size: {} bytes", i + 1, pdfFiles.size(), multipartFile.getSize());

                Path savedPath = fileStorageService.saveOriginal(
                        multipartFile,
                        clientService.getNameById(clientId),
                        "original"
                );
                log.info("[{}/{}] File saved to: {}", i + 1, pdfFiles.size(), savedPath);

                OcrJob job = ocrJobService.create(clientId, name, savedPath.toString());
                log.info("[{}/{}] Job created, id: {}", i + 1, pdfFiles.size(), job.getId());

                ocrAsyncService.start(job.getId());
                log.info("[{}/{}] Job {} started", i + 1, pdfFiles.size(), job.getId());
            }

            log.info("All jobs submitted");
        } catch (IOException e) {
            log.error("Error processing PDFs", e);
        }

        return "redirect:/clients";
    }
}

class PathMultipartFile implements MultipartFile {

    private final Path path;
    private final String originalFilename;
    private final byte[] content;

    public PathMultipartFile(Path path) throws IOException {
        this.path = path;
        this.originalFilename = path.getFileName().toString();
        this.content = Files.readAllBytes(path);
    }

    @Override
    public String getName() {
        return "file";
    }

    @Override
    public String getOriginalFilename() {
        return originalFilename;
    }

    @Override
    public String getContentType() {
        return "application/pdf";
    }

    @Override
    public boolean isEmpty() {
        return content.length == 0;
    }

    @Override
    public long getSize() {
        return content.length;
    }

    @Override
    public byte[] getBytes() {
        return content;
    }

    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(content);
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {
        Files.write(dest.toPath(), content);
    }

    @Override
    public void transferTo(Path dest) throws IOException, IllegalStateException {
        Files.write(dest, content);
    }
}
