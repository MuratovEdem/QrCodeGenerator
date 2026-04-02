package controlm.qrcodegenerator.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Data
public class ClientUpdateRequestDto {

    @NotBlank
    private String name;
    private String innKpp;

    @Valid
    private List<@Valid ContactRequestDto> contacts = new ArrayList<>();
    private List<ConstructionSiteRequestDto> constructionSites = new ArrayList<>();
    private List<ContractRequestDto> contracts = new ArrayList<>();
    private List<UniqueNumberRequestDto> uniqueNumbers = new ArrayList<>();
    private List<MultipartFile> files = new ArrayList<>();
    private List<ClientFileDto> existingFiles = new ArrayList<>();
}
