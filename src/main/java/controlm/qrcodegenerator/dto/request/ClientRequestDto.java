package controlm.qrcodegenerator.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class ClientRequestDto {

    @NotBlank
    private String name;
    private String innKpp;

    @Valid
    private List<@Valid ContactRequestDto> contacts;
    private List<ConstructionSiteRequestDto> constructionSites;
    private List<ContractRequestDto> contracts;
    private List<UniqueNumberRequestDto> uniqueNumbers;
}
