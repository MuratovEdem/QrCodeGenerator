package controlm.qrcodegenerator.service;

import controlm.qrcodegenerator.dto.response.FailedFileAdminDto;
import controlm.qrcodegenerator.dto.response.FailedFileDto;
import controlm.qrcodegenerator.exception.NotFoundException;
import controlm.qrcodegenerator.model.Client;
import controlm.qrcodegenerator.model.FailedFile;
import controlm.qrcodegenerator.repository.FailedFileRepository;
import controlm.qrcodegenerator.utils.TransliterateUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FailedFileService {

    private final FailedFileRepository failedFileRepository;
    private final TransliterateUtils transliterateUtils;
    private final Path root = Paths.get("storage/clients/failed_files");

    public List<FailedFileDto> getFailedFiles(Long clientId) {
        return failedFileRepository.findByClientId(clientId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public FailedFileDto saveFailedFile(Client client, MultipartFile file) throws IOException {
        FailedFile failedFile = new FailedFile();
        failedFile.setFileName(transliterateUtils.transliterateToLatin(file.getOriginalFilename()));
        failedFile.setClient(client);
        failedFile.setFilePath(uploadFailedFile(client, file).toString());
        failedFile.setContentType(file.getContentType());

        return toDto(failedFileRepository.save(failedFile));
    }

    public FailedFile findById(Long fileId) {
        return failedFileRepository.findById(fileId)
                .orElseThrow(() -> new NotFoundException("FailedFile not found with id: " + fileId));
    }

    private Path uploadFailedFile(Client client, MultipartFile file) throws IOException {
        String safeName = transliterateUtils.transliterateToLatin(file.getOriginalFilename());

        String fileName = safeName + ".pdf";

        Path target = createDirectory(client.getName()).resolve(fileName);

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
        }

        return target;
    }

    public List<FailedFileAdminDto> getAllFailedFiles() {
        return failedFileRepository.findAll().stream()
                .map(this::toAdminDto)
                .collect(Collectors.toList());
    }

    private FailedFileDto toDto(FailedFile file) {
        FailedFileDto dto = new FailedFileDto();
        dto.setId(file.getId());
        dto.setFileName(file.getFileName());
        dto.setContentType(file.getContentType());
        return dto;
    }

    private Path createDirectory(String clientName) throws IOException {
        String safeClientFolder = transliterateUtils.transliterateToLatin(clientName);

        Path clientDir = root
                .resolve(safeClientFolder);

        Files.createDirectories(clientDir);

        return clientDir;
    }

    private FailedFileAdminDto toAdminDto(FailedFile file) {
        FailedFileAdminDto dto = new FailedFileAdminDto();
        dto.setId(file.getId());
        dto.setClientId(file.getClient().getId());
        dto.setClientName(file.getClient().getName());
        dto.setFileName(file.getFileName());
        dto.setContentType(file.getContentType());
        return dto;
    }
}
