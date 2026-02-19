package controlm.qrcodegenerator.service;

import controlm.qrcodegenerator.dto.request.ProtocolRequestDto;
import controlm.qrcodegenerator.dto.response.ProtocolPreviewDto;
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
    private final ClientService clientService;
    private final Path root = Paths.get("storage/clients");

    public Path moveToFinalStorage(File tempFile,
                                   ProtocolPreviewDto dto,
                                   Long clientId) throws IOException {

        String safeNumber = transliterateUtils.transliterateToLatin(dto.getNumber());
        String safeDate = dto.getIssueDate();

        String fileName = safeNumber + "_" + safeDate + ".pdf";

        Path target = createDirectory(clientId).resolve(fileName);

        Files.move(tempFile.toPath(),
                target,
                StandardCopyOption.REPLACE_EXISTING);

        return target;
    }

    public Path saveProtocolFile(ProtocolRequestDto dto) throws IOException {
        String safeNumber = transliterateUtils.transliterateToLatin(dto.getFullNumber());
        String safeDate = dto.getIssueDate().toString();

        String fileName = safeNumber + "_" + safeDate + ".pdf";

        Path target = createDirectory(dto.getClientId()).resolve(fileName);

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

    public Path saveOriginal(MultipartFile file, Long clientId) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Файл пуст или не был загружен");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new IllegalArgumentException("Имя файла не может быть пустым");
        }

        String safeFileName = transliterateUtils.transliterateToLatin(originalFilename);

        Path targetDir = createDirectory(clientId).resolve("original");
        Files.createDirectories(targetDir);

        Path targetPath = targetDir.resolve(safeFileName);

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
        }

        return targetPath;
    }

    public void deleteFile(String filePath) throws IOException {
        Files.deleteIfExists(Path.of(filePath));
    }

    private Path createDirectory(Long clientId) throws IOException {
        String clientName = clientService.getClientById(clientId).getName();
        String safeClientFolder = transliterateUtils.transliterateToLatin(clientName);

        Path clientDir = root
                .resolve(safeClientFolder);

        Files.createDirectories(clientDir);

        return clientDir;
    }
}
