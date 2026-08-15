package controlm.qrcodegenerator.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserStatisticsResponseDto {
    private long totalUsers;
    private long activeUsers;
    private long totalProtocols;
    private long totalEdited;
    private List<UserStatisticsDto> users;
}
