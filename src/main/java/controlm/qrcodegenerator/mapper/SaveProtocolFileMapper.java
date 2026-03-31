package controlm.qrcodegenerator.mapper;

import controlm.qrcodegenerator.dto.request.ProtocolRequestDto;
import controlm.qrcodegenerator.dto.request.ProtocolUpdateDto;
import controlm.qrcodegenerator.dto.response.SaveProtocolFileDto;
import controlm.qrcodegenerator.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class SaveProtocolFileMapper {

    private final ClientService clientService;

    public SaveProtocolFileDto protocolRequestDtoToSaveProtocolFileDto(ProtocolRequestDto dto) {
        SaveProtocolFileDto saveProtocolFileDto = new SaveProtocolFileDto();
        saveProtocolFileDto.setFullNumber(dto.getFullNumber());
        saveProtocolFileDto.setIssueDate(dto.getIssueDate());
        saveProtocolFileDto.setFile(dto.getFile());
        saveProtocolFileDto.setClientName(clientService.getNameById(dto.getClientId()));

        return saveProtocolFileDto;
    }

    public SaveProtocolFileDto protocolUpdateDtoToSaveProtocolFileDto(ProtocolUpdateDto dto) {
        SaveProtocolFileDto saveProtocolFileDto = new SaveProtocolFileDto();
        saveProtocolFileDto.setFullNumber(dto.getProtocolNumber());
        saveProtocolFileDto.setIssueDate(dto.getIssueDate());
        saveProtocolFileDto.setFile(dto.getFile());
        saveProtocolFileDto.setClientName(clientService.getNameById(dto.getClientId()));

        return saveProtocolFileDto;
    }

}
