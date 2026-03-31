package controlm.qrcodegenerator.mapper;

import controlm.qrcodegenerator.dto.request.ClientCreateRequestDto;
import controlm.qrcodegenerator.dto.request.ClientUpdateRequestDto;
import controlm.qrcodegenerator.dto.response.ClientResponseDto;
import controlm.qrcodegenerator.dto.response.PublicClientDto;
import controlm.qrcodegenerator.model.*;
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
    private final ClientFileMapper clientFileMapper;

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
        clientResponseDto.setFiles(clientFileMapper.toDtos(client.getClientFiles()));

        return clientResponseDto;
    }

    public ClientUpdateRequestDto toRequestDto(Client client) {
        ClientUpdateRequestDto clientResponseDto = new ClientUpdateRequestDto();

        clientResponseDto.setName(client.getName());
        clientResponseDto.setInnKpp(client.getInnKpp());
        clientResponseDto.setContacts(contactMapper.toRequestDtos(client.getContacts()));
        clientResponseDto.setContracts(contractMapper.toRequestDtos(client.getContracts()));
        clientResponseDto.setUniqueNumbers(uniqueNumberMapper.toRequestDtos(client.getUniqueNumbers()));
        clientResponseDto.setConstructionSites(constructionSiteMapper.toRequestDtos(client.getConstructionSites()));
        clientResponseDto.setExistingFiles(clientFileMapper.toDtos(client.getClientFiles()));

        return clientResponseDto;
    }

    public Client clientCreateRequestDtoToClient(ClientCreateRequestDto dto) {
        Client client = new Client();
        client.setName(dto.getName());
        client.setInnKpp(dto.getInnKpp());

        client.setContacts(new ArrayList<>());
        client.setConstructionSites(new ArrayList<>());
        client.setContracts(new ArrayList<>());
        client.setUniqueNumbers(new ArrayList<>());
        client.setClientFiles(new ArrayList<>());

        dto.getContacts().forEach(cDto -> {
            Contact contact = new Contact();
            contact.setName(cDto.getName());
            contact.setPost(cDto.getPost());
            contact.setPhoneNumber(cDto.getPhoneNumber());
            contact.setEmail(cDto.getEmail());
            contact.setClient(client);
            client.getContacts().add(contact);
        });

        dto.getConstructionSites().forEach(csDto -> {
            ConstructionSite site = new ConstructionSite();
            site.setName(csDto.getName());
            site.setClient(client);
            client.getConstructionSites().add(site);
        });

        dto.getContracts().forEach(cDto -> {
            Contract contract = new Contract();
            contract.setName(cDto.getName());
            contract.setClient(client);
            client.getContracts().add(contract);
        });

        dto.getUniqueNumbers().forEach(uDto -> {
            UniqueNumber num = new UniqueNumber();
            num.setNumber(uDto.getNumber());
            num.setClient(client);
            client.getUniqueNumbers().add(num);
        });
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
