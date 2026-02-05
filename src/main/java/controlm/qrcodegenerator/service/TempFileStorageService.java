package controlm.qrcodegenerator.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
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
        String id = UUID.randomUUID() + ".pdf";
        Path path = tempDir.resolve(id);
        doc.save(path.toFile());
        return id;
    }

    public File get(String id) {
        return tempDir.resolve(id).toFile();
    }

    public void delete(String id) throws IOException {
        Files.deleteIfExists(tempDir.resolve(id));
    }

    public void deleteTempFiles(String[] fileNames) throws IOException {
        for (String fileName : fileNames) {
            delete(fileName);
        }
    }

    public Resource loadTempAsResource(String fileName) throws MalformedURLException {

        Path file = tempDir.resolve(fileName).normalize();

        if (!Files.exists(file)) {
            throw new RuntimeException("Файл не найден: " + fileName);
        }

        return new UrlResource(file.toUri());
    }

    @Scheduled(fixedDelay = 6000000)
    public void cleanTempFiles() {
        log.info("!!!!!");

        Path direct = Paths.get("app/temp-protocols");

        if (!Files.exists(direct)) {
            return;
        }

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(direct)) {
            for (Path file : stream) {
                if (Files.isRegularFile(file)) {
                    try {
                        Instant fileModifiedTime = Files.getLastModifiedTime(file).toInstant();
                        Duration fileAge = Duration.between(fileModifiedTime, Instant.now());

                        if (fileAge.compareTo(Duration.ofMinutes(2)) > 0) {
                            Files.deleteIfExists(file);
                            log.info("Deleted old temp file: {}", file);
                        }
                    } catch (IOException e) {
                        log.error("Error processing file {}: {}", file, e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            log.error("Error accessing temp directory: {}", e.getMessage());
        }
    }

    // TODO починить удаление файлов
}
