package controlm.qrcodegenerator.dto.response;

import lombok.Data;

@Data
public class UserDashboardResponseDto {
    private Long id;
    private String username;
    private String fullName;
    private String roleName;
    private boolean enabled;
    private boolean passwordTemporary;
}
