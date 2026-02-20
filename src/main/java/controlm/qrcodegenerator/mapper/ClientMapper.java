package controlm.qrcodegenerator.mapper;

import controlm.qrcodegenerator.dto.request.ClientRequestDto;
import controlm.qrcodegenerator.dto.response.PublicClientDto;
import controlm.qrcodegenerator.model.Client;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class ClientMapper {

    private final ContactMapper contactMapper;
    private final ContractMapper contractMapper;
    private final ConstructionSiteMapper constructionSiteMapper;
    private final UniqueNumberMapper uniqueNumberMapper;

    public PublicClientDto toClientDto(Client client) {
        PublicClientDto publicClientDto = new PublicClientDto();

        publicClientDto.setId(client.getId());
        publicClientDto.setName(client.getName());

        return publicClientDto;
    }

    public Client clientRequestDtoToClient(ClientRequestDto clientRequestDto) {
        Client client = new Client();

        client.setName(clientRequestDto.getName());
        client.setInnKpp(clientRequestDto.getInnKpp());
        client.setContacts(contactMapper.dtosToContacts(clientRequestDto.getContacts()));
        client.setContracts(contractMapper.dtosToContracts(clientRequestDto.getContracts()));
        client.setConstructionSites(constructionSiteMapper.dtosToConstructionSites(clientRequestDto.getConstructionSites()));
        client.setUniqueNumbers(uniqueNumberMapper.dtosToUniqueNumbers(clientRequestDto.getUniqueNumbers()));

        return client;
    }
}
