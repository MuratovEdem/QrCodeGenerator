package controlm.qrcodegenerator.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class ClientRequestDto {

    private String name;
    private String innKpp;

    private List<ContactRequestDto> contacts;
    private List<ConstructionSiteRequestDto> constructionSites;
    private List<ContractRequestDto> contracts;
    private List<UniqueNumberRequestDto> uniqueNumbers;
}
