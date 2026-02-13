package controlm.qrcodegenerator.service;

import controlm.qrcodegenerator.dto.response.OcrJobResponseDto;
import controlm.qrcodegenerator.enums.OcrJobStatus;
import controlm.qrcodegenerator.exception.NotFoundException;
import controlm.qrcodegenerator.mapper.OcrJobMapper;
import controlm.qrcodegenerator.model.OcrJob;
import controlm.qrcodegenerator.model.User;
import controlm.qrcodegenerator.repository.OcrJobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public Page<OcrJobResponseDto> getPaginatedDtoByUserIdAndStatusNotSaved(Long userId, Pageable pageRequest) {
        Page<OcrJob> jobsPage = ocrJobRepository.findByUserIdAndStatusNot(userId, OcrJobStatus.SAVED, pageRequest);

        return jobsPage.map(ocrJobMapper::ocrToResponseDto);
    }

    public Map<String, Long> getStatusCountsByUserId(Long userId) {
        List<OcrJobStatus> excludedStatuses = List.of(OcrJobStatus.SAVED);

        Map<String, Long> counts = new HashMap<>();
        counts.put("all", ocrJobRepository.countByUserIdAndStatusNotIn(userId, excludedStatuses));
        counts.put("PENDING", ocrJobRepository.countByUserIdAndStatus(userId, OcrJobStatus.PENDING));
        counts.put("PROCESSING", ocrJobRepository.countByUserIdAndStatus(userId, OcrJobStatus.PROCESSING));
        counts.put("DONE", ocrJobRepository.countByUserIdAndStatus(userId, OcrJobStatus.DONE));
        counts.put("ERROR", ocrJobRepository.countByUserIdAndStatus(userId, OcrJobStatus.ERROR));
        counts.put("SAVED", ocrJobRepository.countByUserIdAndStatus(userId, OcrJobStatus.SAVED));

        return counts;
    }

    public void deleteById(Long id) {
        ocrJobRepository.deleteById(id);
    }
}
