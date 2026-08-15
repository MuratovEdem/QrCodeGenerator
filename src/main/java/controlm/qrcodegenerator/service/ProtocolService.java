package controlm.qrcodegenerator.service;

import controlm.qrcodegenerator.dto.request.ProtocolRequestDto;
import controlm.qrcodegenerator.dto.request.ProtocolUpdateDto;
import controlm.qrcodegenerator.dto.response.CipherDto;
import controlm.qrcodegenerator.dto.response.ClientProtocolsViewDto;
import controlm.qrcodegenerator.dto.response.ProtocolPageDto;
import controlm.qrcodegenerator.dto.response.ProtocolPreviewDto;
import controlm.qrcodegenerator.dto.response.ProtocolResponseDto;
import controlm.qrcodegenerator.dto.response.PublicPaginatedProtocolsDto;
import controlm.qrcodegenerator.dto.response.PublicProtocolResponseDto;
import controlm.qrcodegenerator.exception.NotFoundException;
import controlm.qrcodegenerator.mapper.ProtocolMapper;
import controlm.qrcodegenerator.mapper.SaveProtocolFileMapper;
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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProtocolService {
    private final ProtocolRepository protocolRepository;
    private final ProtocolMapper protocolMapper;
    private final FileStorageService fileStorageService;
    private final SaveProtocolFileMapper saveProtocolFileMapper;
    private final CipherService cipherService;

    public Protocol findById(Long id) {
        return protocolRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Protocol not found with id: " + id));
    }

    @Transactional
    public void createProtocol(ProtocolRequestDto protocolRequestDto) throws IOException {
        Protocol protocol = protocolMapper.protocolRequestDtoToProtocol(protocolRequestDto);

        if (existProtocol(protocol, protocolRequestDto.getClientId())) {
            throw new IllegalArgumentException("Протокол с наименованием " + protocol.getFullProtocolNumber() + " уже существует");
        }

        Path path = fileStorageService.saveProtocolFile(saveProtocolFileMapper.protocolRequestDtoToSaveProtocolFileDto(protocolRequestDto));
        protocol.setFilePath(path.toString());
        protocolRepository.save(protocol);
    }

    public Long getNumberNKCipherById(Long id) {
        return protocolRepository.countByProtocolNumberContainingIgnoreCaseAndClientId("НК", id);
    }

    public Long getNumberKBCipherById(Long id) {
        return protocolRepository.countByProtocolNumberContainingIgnoreCaseAndClientId("КБ", id);
    }

    public Long getNumberOtherCipherById(Long id) {
        return protocolRepository.countByProtocolNumberNotLikeAndClientId(id, "НК", "КБ");
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

    @Transactional
    public Protocol updateProtocol(Long id, ProtocolUpdateDto dto) throws IOException {
        Protocol protocol = findById(id);

        protocol.setProtocolNumber(dto.getProtocolNumber().replaceAll(" ", ""));
        protocol.setIssueDate(protocolMapper.parseDate(dto.getIssueDate()));

        if (dto.getFile() == null) {
            return protocolRepository.save(protocol);
        }

        protocol.setFilePath(fileStorageService.replaceProtocolFile(protocol.getFilePath(), saveProtocolFileMapper.protocolUpdateDtoToSaveProtocolFileDto(dto)).toString());

        return protocolRepository.save(protocol);
    }

    @Transactional
    public void deleteProtocolById(Long id) throws IOException {
        fileStorageService.deleteFile(findById(id).getFilePath());
        protocolRepository.deleteById(id);
    }

    public ClientProtocolsViewDto findAllByClientIdWithFilter(
            Long clientId,
            String filter) {

        Map<String, Long> countProtocolsByCipher = protocolRepository.countCiphersByClientId(clientId).stream().collect(Collectors.toMap(
                row -> (String) row[0],
                row -> ((Number) row[1]).longValue()
        ));

        Set<String> ciphers = countProtocolsByCipher.keySet();

        ClientProtocolsViewDto clientProtocolsViewDto = new ClientProtocolsViewDto();
        clientProtocolsViewDto.setUniqueCiphers(ciphers);
        clientProtocolsViewDto.setCountProtocolsByCipher(countProtocolsByCipher);
        clientProtocolsViewDto.setCountTotalProtocols(
                protocolRepository.countByClientId(clientId)
        );
        clientProtocolsViewDto.setSearchQuery(filter);

        return clientProtocolsViewDto;
    }

    public Page<Protocol> findProtocolsByClientIdWithSearchAndCipher(Long clientId, String search, String cipher, Pageable pageable) {
        return protocolRepository
                .findProtocolsByClientIdWithSearchAndCipher(
                        clientId,
                        search,
                        cipher,
                        pageable
                );
    }

    public Protocol getByNumber(String number) {
        return protocolRepository.findByProtocolNumber(number)
                .orElseThrow(() -> new NotFoundException("Protocol not found with number: " + number));
    }

    public ProtocolPageDto getProtocolsByCipher(Long clientId,
                                                String search,
                                                String cipher,
                                                Pageable pageable,
                                                boolean isAdmin) {
        Page<Protocol> pageProtocols = findProtocolsByClientIdWithSearchAndCipher(clientId, search, cipher, pageable);

        List<ProtocolResponseDto> protocols = new ArrayList<>();
        if (isAdmin) {
            protocols = pageProtocols
                    .map(protocolMapper::protocolToProtocolResponseDtoForAdmin)
                    .toList();
        } else {
            protocols = pageProtocols
                    .map(protocolMapper::protocolToProtocolResponseDto)
                    .toList();
        }

        ProtocolPageDto protocolPageDto = new ProtocolPageDto();

        protocolPageDto.setProtocols(protocols);
        protocolPageDto.setTotalPages(pageProtocols.getTotalPages());
        protocolPageDto.setCurrentPage(pageProtocols.getNumber());

        return protocolPageDto;
    }

    public PublicPaginatedProtocolsDto getFilteredAndPaginatedDtoForPublic(Long clientId,
                                                                           String filter,
                                                                           Pageable pageable) {
        Page<Protocol> protocolPage = protocolRepository.findProtocolsByClientIdWithSearch(clientId, filter, pageable);

        List<CipherDto> allCiphers = cipherService.findAll();
        Map<String, String> cipherDescMap = allCiphers.stream()
                .collect(Collectors.toMap(
                        c -> c.getName().trim().toUpperCase(),
                        CipherDto::getDescription,
                        (a, b) -> a));

        List<PublicProtocolResponseDto> protocols = protocolPage
                .map(protocol -> protocolMapper.protocolToPublicProtocolResponseDto(protocol, cipherDescMap))
                .toList();

        PublicPaginatedProtocolsDto dto = new PublicPaginatedProtocolsDto();
        dto.setProtocols(protocols);
        dto.setCurrentPage(protocolPage.getNumber());
        dto.setTotalPages(protocolPage.getTotalPages());
        dto.setSearchQuery(filter);
        return dto;
    }

    public long getNumberProtocolsByCreatedBy(String username) {
        return protocolRepository.countByCreatedBy(username);
    }

    public boolean existByProtocolNumberAndClientId(ProtocolPreviewDto dto, Long clientId) {
        return protocolRepository.existsByProtocolNumberAndClientId(
                dto.getNumber(),
                clientId);
    }


    private boolean existProtocol(Protocol protocol, Long clientId) {
        return protocolRepository.existsByProtocolNumberAndClientId(
                protocol.getProtocolNumber(),
                clientId);
    }

    private Optional<Protocol> findExistingProtocol(Protocol protocol, Long clientId) {
        return protocolRepository.findByProtocolNumberAndClientId(
                protocol.getProtocolNumber(),
                clientId);
    }
}
