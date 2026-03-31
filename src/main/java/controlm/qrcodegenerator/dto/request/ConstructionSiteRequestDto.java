package controlm.qrcodegenerator.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ConstructionSiteRequestDto {

    private Long id;
    @NotBlank(message = "Наименование объекта обязательно")
    private String name;
}
