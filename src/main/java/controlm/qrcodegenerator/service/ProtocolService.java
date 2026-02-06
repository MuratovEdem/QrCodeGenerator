package controlm.qrcodegenerator.service;

import controlm.qrcodegenerator.dto.request.ProtocolRequestDto;
import controlm.qrcodegenerator.dto.response.ProtocolHistoryDto;
import controlm.qrcodegenerator.dto.response.ProtocolResponseDto;
import controlm.qrcodegenerator.exception.NotFoundException;
import controlm.qrcodegenerator.mapper.ProtocolMapper;
import controlm.qrcodegenerator.model.Protocol;
import controlm.qrcodegenerator.repository.ProtocolRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProtocolService {
    private final ProtocolRepository protocolRepository;
    private final ProtocolMapper protocolMapper;
    private final FileStorageService fileStorageService;

    public List<ProtocolResponseDto> findAllByClientId(Long clientId) {
        List<Protocol> protocols = protocolRepository.findByClientId(clientId);

        return protocolMapper.protocolsToProtocolsDto(protocols);
    }

    public Protocol findById(Long id) {
        return protocolRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Client not found with id: " + id));
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

        return history;
    }

    public void createProtocols(ProtocolRequestDto protocolRequestDto) {

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
                Protocol protocol = protocolMapper.protocolRequestDtoToProtocol(protocolRequestDto, String.valueOf(i));

                if (existProtocol(protocol, protocolRequestDto.getClientId())) {
                    throw new IllegalArgumentException("Протокол с наименованием " + protocol.getFullProtocolNumber() + " уже существует");
                }
                protocolRepository.save(protocol);
            }
        } else {
            Protocol protocol = protocolMapper.protocolRequestDtoToProtocol(protocolRequestDto,
                    protocolRequestDto.getSequentialNumber());

            if (existProtocol(protocol, protocolRequestDto.getClientId())) {
                throw new IllegalArgumentException("Протокол с наименованием " + protocol.getFullProtocolNumber() + " уже существует");
            }

            protocolRepository.save(protocol);
        }
    } // TODO убрать сохранение диапазоном ??

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

    public Resource getProtocolFile(Long protocolId) throws MalformedURLException {
        Protocol protocol = findById(protocolId);

        return fileStorageService.loadAsResource(protocol.getFilePath());
    }

    public String getProtocolFileName(Long protocolId) {
        Protocol protocol = findById(protocolId);

        return Paths.get(protocol.getFilePath()).getFileName().toString();
    }

    public void createProtocolFromPdf(Long clientId, String number, String issueDate, String pathFile) {

        Protocol protocol = protocolMapper.fieldsToProtocol(clientId, number, issueDate, pathFile);

        log.info("{}, {}, {}, {}, {}", protocol.getCipher(), protocol.getUniqueNumber(), protocol.getSequentialNumber(), protocol.getIssueDate(), protocol.getFilePath());

        if (existProtocol(protocol, clientId)) {
            throw new IllegalArgumentException("Протокол с наименованием " + protocol.getFullProtocolNumber() + " уже существует");
        }

        protocolRepository.save(protocol);
        // TODO: сделать проверку на совпадения
    }

    public Protocol updateProtocol(Long id, ProtocolRequestDto dto) {
        Protocol protocol = findById(id);

        protocol.setSequentialNumber(dto.getSequentialNumber());
        protocol.setCipher(dto.getCipher());
        protocol.setUniqueNumber(dto.getUniqueNumber());

        return protocolRepository.save(protocol);

        // TODO дата, файл
    }

    public void deleteProtocolById(Long id) {
        protocolRepository.deleteById(id);

        // TODO файл
    }

    private boolean existProtocol(Protocol protocol, Long clientId) {
        return protocolRepository.existsByCipherAndUniqueNumberAndSequentialNumberAndClientId(
                protocol.getCipher(),
                protocol.getUniqueNumber(),
                protocol.getSequentialNumber(),
                clientId);
    }
}
