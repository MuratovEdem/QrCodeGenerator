package controlm.qrcodegenerator.mapper;

import controlm.qrcodegenerator.dto.response.PublicClientDto;
import controlm.qrcodegenerator.model.Client;
import org.springframework.stereotype.Component;

@Component
public class ClientMapper {

    public PublicClientDto toClientDto(Client client) {
        PublicClientDto publicClientDto = new PublicClientDto();

        publicClientDto.setId(client.getId());
        publicClientDto.setName(client.getName());

        return publicClientDto;
    }
}
