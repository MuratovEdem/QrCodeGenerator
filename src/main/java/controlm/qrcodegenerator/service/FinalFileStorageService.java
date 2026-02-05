package controlm.qrcodegenerator.service;

import controlm.qrcodegenerator.dto.response.ProtocolPreviewDto;
import controlm.qrcodegenerator.utils.TransliterateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Slf4j
@Service
@RequiredArgsConstructor
public class FinalFileStorageService {

    private final TransliterateUtils transliterateUtils;
    private final ClientService clientService;
    private final Path root = Paths.get("storage/clients");

    public Path moveToFinalStorage(File tempFile,
                                   ProtocolPreviewDto dto,
                                   Long clientId) throws IOException {

        String clientName = clientService.getClientById(clientId).getName();
        String safeClientFolder = transliterateUtils.transliterateToLatin(clientName);
        String safeNumber = transliterateUtils.transliterateToLatin(dto.getNumber());
        String safeDate = dto.getIssueDate();

        String fileName = safeNumber + "_" + safeDate + ".pdf";

        Path clientDir = root
                .resolve(safeClientFolder);

        Files.createDirectories(clientDir);

        Path target = clientDir.resolve(fileName);

        Files.move(tempFile.toPath(),
                target,
                StandardCopyOption.REPLACE_EXISTING);

        return target;
    }
}
