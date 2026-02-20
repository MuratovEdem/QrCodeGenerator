package controlm.qrcodegenerator.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UniqueNumberRequestDto {
    @NotNull
    private Long number;
}
