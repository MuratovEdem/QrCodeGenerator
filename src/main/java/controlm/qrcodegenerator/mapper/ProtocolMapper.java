package controlm.qrcodegenerator.mapper;

import controlm.qrcodegenerator.dto.response.ProtocolResponseDto;
import controlm.qrcodegenerator.model.Protocol;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProtocolMapper {

    public List<ProtocolResponseDto> protocolsToProtocolsDto(List<Protocol> protocols) {
        List<ProtocolResponseDto> protocolResponseDtos = new ArrayList<>();
        for (int i = 0; i < protocols.size(); i++) {
            ProtocolResponseDto protocolResponseDto = new ProtocolResponseDto();
            protocolResponseDto.setName(protocols.get(i).getFullProtocolNumber());
            protocolResponseDtos.add(protocolResponseDto);
        }
        return protocolResponseDtos;
    }
}
