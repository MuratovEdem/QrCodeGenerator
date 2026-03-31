package controlm.qrcodegenerator.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UniqueNumberRequestDto {

    private Long id;
    @NotNull
    private Long number;
}
