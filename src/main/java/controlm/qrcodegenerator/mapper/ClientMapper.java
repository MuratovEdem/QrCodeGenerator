package controlm.qrcodegenerator.mapper;

import controlm.qrcodegenerator.dto.request.ClientRequestDto;
import controlm.qrcodegenerator.dto.response.ClientResponseDto;
import controlm.qrcodegenerator.dto.response.PublicClientDto;
import controlm.qrcodegenerator.model.Client;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Component
public class ClientMapper {

    private final ContactMapper contactMapper;
    private final ContractMapper contractMapper;
    private final ConstructionSiteMapper constructionSiteMapper;
    private final UniqueNumberMapper uniqueNumberMapper;

    public PublicClientDto toPublicClientDto(Client client) {
        PublicClientDto publicClientDto = new PublicClientDto();

        publicClientDto.setId(client.getId());
        publicClientDto.setName(client.getName());

        return publicClientDto;
    }

    public ClientResponseDto toResponseDto(Client client) {
        ClientResponseDto clientResponseDto = new ClientResponseDto();

        clientResponseDto.setId(client.getId());
        clientResponseDto.setName(client.getName());
        clientResponseDto.setContacts(contactMapper.toResponseDtos(client.getContacts()));
        clientResponseDto.setContracts(contractMapper.toResponseDtos(client.getContracts()));
        clientResponseDto.setInnKpp(client.getInnKpp());
        clientResponseDto.setUniqueNumbers(uniqueNumberMapper.toResponseDtos(client.getUniqueNumbers()));
        clientResponseDto.setConstructionSites(constructionSiteMapper.toResponseDtos(client.getConstructionSites()));

        return clientResponseDto;
    }

    public Client clientRequestDtoToClient(ClientRequestDto clientRequestDto) {
        Client client = new Client();

        client.setName(clientRequestDto.getName());
        client.setInnKpp(clientRequestDto.getInnKpp());
        client.setContacts(contactMapper.dtosToContacts(safeList(clientRequestDto.getContacts())));
        client.setContracts(contractMapper.dtosToContracts(safeList(clientRequestDto.getContracts())));
        client.setConstructionSites(constructionSiteMapper.dtosToConstructionSites(safeList(clientRequestDto.getConstructionSites())));
        client.setUniqueNumbers(uniqueNumberMapper.dtosToUniqueNumbers(safeList(clientRequestDto.getUniqueNumbers())));

        return client;
    }

    private <T> List<T> safeList(List<T> list) {
        if (list == null) {
            return new ArrayList<>();
        }
        return list.stream()
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
