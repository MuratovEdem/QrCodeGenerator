package controlm.qrcodegenerator.mapper;

import controlm.qrcodegenerator.dto.response.OcrJobResponseDto;
import controlm.qrcodegenerator.model.OcrJob;
import controlm.qrcodegenerator.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.file.Path;

@Component
@RequiredArgsConstructor
public class OcrJobMapper {

    private final ClientService clientService;

    public OcrJobResponseDto ocrToResponseDto(OcrJob ocrJob) {
        OcrJobResponseDto dto = new OcrJobResponseDto();

        Path path = Path.of(ocrJob.getOriginalFilePath());

        dto.setId(ocrJob.getId());
        dto.setCreatedAt(ocrJob.getCreatedAt());
        dto.setClientId(ocrJob.getClientId());
        dto.setCreatedBy(ocrJob.getCreatedBy());
        dto.setStatus(ocrJob.getStatus());
        dto.setErrorMessage(ocrJob.getErrorMessage());
        dto.setUserId(ocrJob.getUserId());
        dto.setFinishedAt(ocrJob.getFinishedAt());
        dto.setOriginalFileName(path.getFileName().toString());
        dto.setPath(path.toString());
        dto.setClientName(clientService.getClientById(ocrJob.getClientId()).getName());
        dto.setProgress(0);

        return dto;
    }
}
