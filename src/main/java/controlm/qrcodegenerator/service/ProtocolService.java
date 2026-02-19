package controlm.qrcodegenerator.service;

import controlm.qrcodegenerator.dto.request.ProtocolRequestDto;
import controlm.qrcodegenerator.dto.response.*;
import controlm.qrcodegenerator.exception.NotFoundException;
import controlm.qrcodegenerator.mapper.ProtocolMapper;
import controlm.qrcodegenerator.model.Protocol;
import controlm.qrcodegenerator.repository.ProtocolRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProtocolService {
    private final ProtocolRepository protocolRepository;
    private final ProtocolMapper protocolMapper;
    private final FileStorageService fileStorageService;

    public List<PublicProtocolResponseDto> findAllByClientId(Long clientId) {
        List<Protocol> protocols = protocolRepository.findByClientId(clientId);

        return protocolMapper.protocolsToPublicProtocolsDto(protocols);
    }

    public Protocol findById(Long id) {
        return protocolRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Client not found with id: " + id));
    }

    @Transactional
    public void createProtocol(ProtocolRequestDto protocolRequestDto) throws IOException {

        if (protocolRepository.existsByUniqueNumberAndClientIdNot(protocolRequestDto.getUniqueNumber(),
                protocolRequestDto.getClientId())) {
            throw new IllegalArgumentException("Такой номер клиента уже занят");
        }

        Protocol protocol = protocolMapper.protocolRequestDtoToProtocol(
                protocolRequestDto,
                protocolRequestDto.getSequentialNumber());

        if (existProtocol(protocol, protocolRequestDto.getClientId())) {
            throw new IllegalArgumentException("Протокол с наименованием " + protocol.getFullProtocolNumber() + " уже существует");
        }

        fileStorageService.saveProtocolFile(protocolRequestDto);
        protocolRepository.save(protocol);
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

        Optional<Protocol> existingProtocol = findExistingProtocol(protocol, clientId);

        existingProtocol.ifPresent(value -> protocol.setId(value.getId()));

        protocolRepository.save(protocol);
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

    public List<PublicProtocolResponseDto> getFilteredProtocolsByClientId(Long clientId, String filter) {
        List<PublicProtocolResponseDto> protocols = findAllByClientId(clientId);

        if (filter != null && !filter.trim().isEmpty()) {

            String searchTerm = filter.trim().toLowerCase();
            protocols = protocols.stream()
                    .filter(protocol -> (protocol.getFullProtocolNumber() != null &&
                            protocol.getFullProtocolNumber().toLowerCase().contains(searchTerm))
                    )
                    .toList();
        }

        return protocols;
    }

    public ClientProtocolsViewDto findAllByClientIdWithFilter(Long clientId, String filter, Pageable pageable) {
        List<String> distinctCiphersByClientId = protocolRepository.findDistinctCiphersByClientId(clientId);

        Map<String, List<ProtocolResponseDto>> protocolsByCipher = new HashMap<>();
        Map<String, Long> countProtocolsByCipher = new HashMap<>();
        Page<Protocol> biggestPage = Page.empty();

        int pageCounter = -1;
        for (String cipher : distinctCiphersByClientId) {
            Page<Protocol> pageProtocols = protocolRepository.findProtocolsByClientIdWithSearchAndCipher(
                    clientId,
                    filter,
                    cipher,
                    pageable);

            if (!pageProtocols.getContent().isEmpty()) {
                if (pageProtocols.getTotalPages() > pageCounter) {
                    biggestPage = pageProtocols;
                    pageCounter = pageProtocols.getTotalPages();
                }

                List<ProtocolResponseDto> protocols = pageProtocols
                        .map(protocolMapper::protocolToProtocolResponseDto)
                        .get().toList();

                protocolsByCipher.put(cipher, protocols);
                countProtocolsByCipher.put(cipher, pageProtocols.getTotalElements());
            }

        }

        ClientProtocolsViewDto clientProtocolsViewDto = new ClientProtocolsViewDto();

        clientProtocolsViewDto.setProtocolsByCipher(protocolsByCipher);
        clientProtocolsViewDto.setUniqueCiphers(protocolsByCipher.keySet());
        clientProtocolsViewDto.setCountProtocolsByCipher(countProtocolsByCipher);
        clientProtocolsViewDto.setCountTotalProtocols(protocolRepository.countByClientId(clientId));
        clientProtocolsViewDto.setCurrentPage(biggestPage.getNumber());
        clientProtocolsViewDto.setPageSize(biggestPage.getSize());
        clientProtocolsViewDto.setTotalPages(biggestPage.getTotalPages());
        clientProtocolsViewDto.setSearchQuery(filter);

        log.info("{}", clientProtocolsViewDto);
        // TODO придумать что делать с широкой таблицей

        return clientProtocolsViewDto; //TODO упорядочить метод
    }

    public PublicPaginatedProtocolsDto getFilteredAndPaginatedDtoForPublic(Long clientId,
                                                                           String filter,
                                                                           int page,
                                                                           int pageSize) {
        List<PublicProtocolResponseDto> filteredProtocols = getFilteredProtocolsByClientId(clientId, filter);

        List<PublicProtocolResponseDto> paginatedProtocols = getPaginatedProtocols(page, pageSize, filteredProtocols);
        log.info(paginatedProtocols.toString());

        int totalPages = (int) Math.ceil((double) filteredProtocols.size() / pageSize);

        PublicPaginatedProtocolsDto dto = new PublicPaginatedProtocolsDto();
        dto.setProtocols(paginatedProtocols);
        dto.setCountProtocols(filteredProtocols.size());
        dto.setCurrentPage(page);
        dto.setPageSize(pageSize);
        dto.setTotalPages(totalPages);
        dto.setSearchQuery(filter);
        return dto;
    }

    public boolean existByCipherAndUniqueNumberAndSequenceNumber(ProtocolPreviewDto dto, Long clientId) {
        ProtocolNumberDto numberDto = protocolMapper.parseNumberToNumberDto(dto.getNumber()).orElseThrow(
                () -> new NotFoundException("Parse protocol number exception"));

        return protocolRepository.existsByCipherAndUniqueNumberAndSequentialNumberAndClientId(
                numberDto.getCipher(),
                numberDto.getUniqueNumber(),
                numberDto.getSequentialNumber(),
                clientId);
    }

    private List<PublicProtocolResponseDto> getPaginatedProtocols(int page, int pageSize, List<PublicProtocolResponseDto> protocols) {
        List<PublicProtocolResponseDto> paginatedProtocols = new ArrayList<>();

        int fromIndex = page * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, protocols.size());

        for (int i = fromIndex; i < toIndex; i++) {
            paginatedProtocols.add(protocols.get(i));
        }

        return paginatedProtocols;
    }

    private boolean existProtocol(Protocol protocol, Long clientId) {
        return protocolRepository.existsByCipherAndUniqueNumberAndSequentialNumberAndClientId(
                protocol.getCipher(),
                protocol.getUniqueNumber(),
                protocol.getSequentialNumber(),
                clientId);
    }

    private Optional<Protocol> findExistingProtocol(Protocol protocol, Long clientId) {
        return protocolRepository.findByCipherAndUniqueNumberAndSequentialNumberAndClientId(
                protocol.getCipher(),
                protocol.getUniqueNumber(),
                protocol.getSequentialNumber(),
                clientId);
    }
}
