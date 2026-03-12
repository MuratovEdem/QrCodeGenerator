package controlm.qrcodegenerator.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class ClientResponseDto {

    private Long id;
    private String name;
    private String innKpp;

    private List<ContactResponseDto> contacts;
    private List<ConstructionSiteResponseDto> constructionSites;
    private List<ContractResponseDto> contracts;
    private List<UniqueNumberResponseDto> uniqueNumbers;
}
