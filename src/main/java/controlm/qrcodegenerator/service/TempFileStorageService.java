package controlm.qrcodegenerator.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@Slf4j
@EnableScheduling
public class TempFileStorageService {

    private final Path tempDir = Paths.get("temp-protocols");

    public TempFileStorageService() throws IOException {
        Files.createDirectories(tempDir);
    }

    public String saveTemp(PDDocument doc) throws IOException {
        String tempName = UUID.randomUUID() + ".pdf";
        Path path = tempDir.resolve(tempName);
        doc.save(path.toFile());
        return tempName;
    }

    public File get(String tempName) {
        return tempDir.resolve(tempName).toFile();
    }

    public void delete(String tempName) throws IOException {
        Files.deleteIfExists(tempDir.resolve(tempName));
    }

    public Resource loadTempAsResource(String fileName) throws MalformedURLException {

        Path file = tempDir.resolve(fileName).normalize();

        if (!Files.exists(file)) {
            throw new RuntimeException("Файл не найден: " + fileName);
        }

        return new UrlResource(file.toUri());
    }
}
