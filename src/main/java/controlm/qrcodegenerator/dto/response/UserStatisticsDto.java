package controlm.qrcodegenerator.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserStatisticsDto {
    private Long userId;
    private String name;
    private String surname;
    private long createdCount;
    private long editedCount;
    private long totalOperations;
}
