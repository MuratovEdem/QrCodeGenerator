package controlm.qrcodegenerator.mapper;

import controlm.qrcodegenerator.dto.response.ClientDto;
import controlm.qrcodegenerator.model.Client;
import org.springframework.stereotype.Component;

@Component
public class ClientMapper {

    public ClientDto toClientDto(Client client) {
        ClientDto clientDto = new ClientDto();

        clientDto.setId(client.getId());
        clientDto.setName(client.getName());

        return clientDto;
    }
}
