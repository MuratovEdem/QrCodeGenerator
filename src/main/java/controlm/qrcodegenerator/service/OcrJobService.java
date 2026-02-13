package controlm.qrcodegenerator.service;

import controlm.qrcodegenerator.dto.response.OcrJobResponseDto;
import controlm.qrcodegenerator.enums.OcrJobStatus;
import controlm.qrcodegenerator.exception.NotFoundException;
import controlm.qrcodegenerator.mapper.OcrJobMapper;
import controlm.qrcodegenerator.model.OcrJob;
import controlm.qrcodegenerator.model.User;
import controlm.qrcodegenerator.repository.OcrJobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OcrJobService {

    private final OcrJobRepository ocrJobRepository;
    private final UserService userService;
    private final OcrJobMapper ocrJobMapper;

    public OcrJob findById(Long jobId) {
        return ocrJobRepository.findById(jobId).orElseThrow(
                () -> new NotFoundException("OcrJob not found with id: " + jobId));
    }

    public OcrJobResponseDto getDtoById(Long jobId) {
        return ocrJobMapper.ocrToResponseDto(findById(jobId));
    }

    public OcrJob save(OcrJob ocrJob) {
        return ocrJobRepository.save(ocrJob);
    }

    public OcrJob create(Long clientId, String username, String filePath) {
        User user = userService.findByUsername(username);
        OcrJob job = new OcrJob();
        job.setClientId(clientId);
        job.setCreatedBy(user.getUsername());
        job.setUserId(user.getId());

        job.setOriginalFilePath(filePath);
        job.setStatus(OcrJobStatus.PENDING);
        job.setCreatedAt(LocalDateTime.now());

        return save(job);
    }

    public List<OcrJobResponseDto> getDtosByUserIdAndStatusNotSaved(Long userId) {
        List<OcrJob> allByUserIdAndStatusNot = ocrJobRepository.findAllByUserIdAndStatusNot(userId, OcrJobStatus.SAVED);

        return ocrJobMapper.ocrListToResponseDtos(allByUserIdAndStatusNot);
    }

    public List<OcrJob> findOcrJobsByUserId(Long userId) {
        return ocrJobRepository.findAllByUserId(userId);
    }

    public void deleteById(Long id) {
        ocrJobRepository.deleteById(id);
    }
}
