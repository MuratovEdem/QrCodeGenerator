package controlm.qrcodegenerator.service;

import controlm.qrcodegenerator.dto.response.AuditLogDto;
import controlm.qrcodegenerator.model.AuditLog;
import controlm.qrcodegenerator.repository.AuditLogRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AuditLogService {
    private final AuditLogRepository auditLogRepository;

    public List<AuditLogDto> findAllByOrderByPerformedAtDesc() {
        List<AuditLog> logs = auditLogRepository.findAllByOrderByCreatedAtDesc();
        return logs.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    public AuditLogDto convertToDto(AuditLog log) {
        AuditLogDto dto = new AuditLogDto();
        dto.setAction(log.getAction());
        dto.setTargetUsername(log.getTargetUsername());
        dto.setDetails(log.getDetails());
        dto.setCreatedBy(log.getCreatedBy());
        dto.setCreatedAt(log.getCreatedAt());
        return dto;
    }
}
