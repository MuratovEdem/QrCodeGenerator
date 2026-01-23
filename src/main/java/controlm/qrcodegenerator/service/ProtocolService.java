package controlm.qrcodegenerator.service;

import controlm.qrcodegenerator.dto.request.ProtocolRequestDto;
import controlm.qrcodegenerator.dto.response.ProtocolHistoryDto;
import controlm.qrcodegenerator.dto.response.ProtocolResponseDto;
import controlm.qrcodegenerator.mapper.ProtocolMapper;
import controlm.qrcodegenerator.model.Client;
import controlm.qrcodegenerator.model.Protocol;
import controlm.qrcodegenerator.repository.ProtocolRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
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

    @Transactional(readOnly = true)
    public ProtocolHistoryDto getProtocolHistoryByClientId(Long id) {
        Optional<Protocol> protocols = protocolRepository.findFirstByClientIdOrderByCreatedAtDesc(id);

        ProtocolHistoryDto history = new ProtocolHistoryDto();

        if (protocols.isPresent()) {
            Protocol lastProtocol = protocols.get();
            history.setLastCipher(lastProtocol.getCipher());
            history.setLastUniqueNumber(lastProtocol.getUniqueNumber());
        }

        List<String> cipherHistory = protocolRepository.findDistinctCiphersByClientId(id);

        List<String> uniqueNumberHistory = protocolRepository.findDistinctUniqueNumberByClientId(id);
        history.setCipherHistory(cipherHistory);
        history.setUniqueNumberHistory(uniqueNumberHistory);

        log.info("cipherHistory {}, uniqueNumberHistory {}, LastCipher {}, LastUniqueNumber {}",
                cipherHistory, uniqueNumberHistory, history.getLastCipher(), history.getUniqueNumberHistory());

        return history;
    }

    public void createProtocols(ProtocolRequestDto protocolRequestDto) {
        Client client = clientService.getClientById(protocolRequestDto.getClientId());

        if (protocolRepository.existsByUniqueNumberAndClientIdNot(protocolRequestDto.getUniqueNumber(),
                protocolRequestDto.getClientId())) {
            throw new IllegalArgumentException("Такой номер клиента уже занят");
        }

        if (protocolRequestDto.getSequentialNumber().contains("-")) {
            String[] parts = protocolRequestDto.getSequentialNumber().split("-", 0);
            int start = Integer.parseInt(parts[0]);
            int end = Integer.parseInt(parts[1]);

            if (start > end) {
                throw new IllegalArgumentException("Диапазон не может быть отрицательным");
            }

            for (int i = start; i <= end; i++) {
                Protocol protocol = new Protocol();
                protocol.setCipher(protocolRequestDto.getCipher());
                protocol.setUniqueNumber(protocolRequestDto.getUniqueNumber());
                protocol.setSequentialNumber((long) i);
                protocol.setClient(client);

                if (protocolRepository.existsByCipherAndUniqueNumberAndSequentialNumberAndClientId(
                        protocol.getCipher(),
                        protocol.getUniqueNumber(),
                        protocol.getSequentialNumber(),
                        protocolRequestDto.getClientId())) {
                    throw new IllegalArgumentException("Протокол с наименованием " + protocol.getFullProtocolNumber() + " уже существует");
                }
                protocolRepository.save(protocol);
            }
        } else {
            Protocol protocol = new Protocol();
            protocol.setCipher(protocolRequestDto.getCipher());
            protocol.setUniqueNumber(protocolRequestDto.getUniqueNumber());
            protocol.setSequentialNumber(Long.valueOf(protocolRequestDto.getSequentialNumber()));
            protocol.setClient(client);

            if (protocolRepository.existsByCipherAndUniqueNumberAndSequentialNumberAndClientId(
                    protocol.getCipher(),
                    protocol.getUniqueNumber(),
                    protocol.getSequentialNumber(),
                    protocolRequestDto.getClientId())) {
                throw new IllegalArgumentException("Протокол с наименованием " + protocol.getFullProtocolNumber() + " уже существует");
            }

            protocolRepository.save(protocol);
        }
    }

    public Long getNumberNKCipherById(Long id) {
        return protocolRepository.countByCipherAndClientId("НК", id);
    }

    public Long getNumberKBCipherById(Long id) {
        return protocolRepository.countByCipherAndClientId("КБ", id);
    }

    public Long getNumberOtherCipherById(Long id) {
        List<String> excludedCiphers = new ArrayList<>();
        excludedCiphers.add("НК");
        excludedCiphers.add("КБ");
        return protocolRepository.countByCipherNotInAndClientId(excludedCiphers, id);
    }
}
