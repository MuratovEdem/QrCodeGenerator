package controlm.qrcodegenerator.mapper;

import controlm.qrcodegenerator.dto.request.ClientFileDto;
import controlm.qrcodegenerator.model.ClientFile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ClientFileMapper {

    public ClientFileDto toDto(ClientFile clientFile) {
        ClientFileDto clientFileDto = new ClientFileDto();

        clientFileDto.setId(clientFile.getId());
        clientFileDto.setFileName(clientFile.getFileName());
        clientFileDto.setContentType(clientFile.getContentType());
        clientFileDto.setFilePath(clientFile.getFilePath());

        return clientFileDto;
    }

    public List<ClientFileDto> toDtos(List<ClientFile> clientFiles) {
        List<ClientFileDto> clientFileDtos = new ArrayList<>();

        for (ClientFile clientFile : clientFiles) {
            clientFileDtos.add(toDto(clientFile));
        }

        return clientFileDtos;
    }
}
