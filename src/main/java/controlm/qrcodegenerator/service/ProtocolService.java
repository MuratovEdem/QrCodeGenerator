package controlm.qrcodegenerator.service;

import controlm.qrcodegenerator.dto.request.ProtocolRequestDto;
import controlm.qrcodegenerator.dto.response.ProtocolResponseDto;
import controlm.qrcodegenerator.mapper.ProtocolMapper;
import controlm.qrcodegenerator.model.Client;
import controlm.qrcodegenerator.model.Protocol;
import controlm.qrcodegenerator.repository.ProtocolRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProtocolService {
    private final ProtocolRepository protocolRepository;
    private final ProtocolMapper protocolMapper;
    private final ClientService clientService;

    public List<ProtocolResponseDto> findByClientId(Long clientId) {
        List<Protocol> protocols = protocolRepository.findByClientId(clientId);

        return protocolMapper.protocolsToProtocolsDto(protocols);
    }

    public Protocol createProtocolByClientId(ProtocolRequestDto protocolRequestDto, Long clientId) {
        Client client = clientService.getClientById(clientId);
        Protocol protocol = new Protocol();
        protocol.setName(protocolRequestDto.getName());
        protocol.setClient(client);

        return protocolRepository.save(protocol);
    }
}
