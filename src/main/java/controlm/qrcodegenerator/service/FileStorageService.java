package controlm.qrcodegenerator.service;

import controlm.qrcodegenerator.dto.response.ProtocolPreviewDto;
import controlm.qrcodegenerator.dto.response.SaveProtocolFileDto;
import controlm.qrcodegenerator.utils.TransliterateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Slf4j
@RequiredArgsConstructor
@Service
public class FileStorageService {

    private final TransliterateUtils transliterateUtils;
    private final Path root = Paths.get("storage/clients");

    public Path moveToFinalStorage(File tempFile,
                                   ProtocolPreviewDto dto,
                                   String clientName) throws IOException {

        String safeNumber = transliterateUtils.transliterateToLatin(dto.getNumber());
        String safeDate = dto.getIssueDate();

        String fileName = safeNumber + "_" + safeDate + ".pdf";

        Path target = createDirectory(clientName).resolve(fileName);

        Files.move(tempFile.toPath(),
                target,
                StandardCopyOption.REPLACE_EXISTING);

        return target;
    }

    public Path saveProtocolFile(SaveProtocolFileDto dto) throws IOException {
        String safeNumber = transliterateUtils.transliterateToLatin(dto.getFullNumber());
        String safeDate = dto.getIssueDate();

        String fileName = safeNumber + "_" + safeDate + ".pdf";

        Path target = createDirectory(dto.getClientName()).resolve(fileName);

        try (InputStream inputStream = dto.getFile().getInputStream()) {
            Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
        }

        return target;
    }

    public Resource loadAsResource(String path) throws MalformedURLException {
        Path file = Paths.get(path);

        if (!Files.exists(file)) {
            throw new RuntimeException("Файл не найден: " + path);
        }

        return new UrlResource(file.toUri());
    }

    public Path saveOriginal(MultipartFile file, String clientName, String target) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Файл пуст или не был загружен");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new IllegalArgumentException("Имя файла не может быть пустым");
        }

        String safeFileName = transliterateUtils.transliterateToLatin(originalFilename);

        Path targetDir = createDirectory(clientName).resolve(target);
        Files.createDirectories(targetDir);

        Path targetPath = targetDir.resolve(safeFileName);

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }

        return targetPath;
    }

    public void deleteFile(String filePath){
        try {
            Files.deleteIfExists(Path.of(filePath));
        } catch (IOException e) {
            log.error("Ошибка при удалении физического файла: {}", filePath, e);
        }
    }

    public Path replaceProtocolFile(String oldFilePath, SaveProtocolFileDto dto) throws IOException {

        if (oldFilePath != null && !oldFilePath.trim().isEmpty()) {
            Path oldPath = Paths.get(oldFilePath);
            Files.deleteIfExists(oldPath);
        }

        return saveProtocolFile(dto);
    }

    private Path createDirectory(String clientName) throws IOException {
        String safeClientFolder = transliterateUtils.transliterateToLatin(clientName);

        Path clientDir = root
                .resolve(safeClientFolder);

        Files.createDirectories(clientDir);

        return clientDir;
    }
}
