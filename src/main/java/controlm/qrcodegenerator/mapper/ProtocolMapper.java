package controlm.qrcodegenerator.mapper;

import controlm.qrcodegenerator.dto.request.ProtocolRequestDto;
import controlm.qrcodegenerator.dto.response.ProtocolNumberDto;
import controlm.qrcodegenerator.dto.response.ProtocolResponseDto;
import controlm.qrcodegenerator.dto.response.PublicProtocolResponseDto;
import controlm.qrcodegenerator.model.Protocol;
import controlm.qrcodegenerator.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProtocolMapper {

    private final ClientService clientService;

    private final Pattern PROTOCOL_PATTERN =
            Pattern.compile("^([А-ЯA-Z]{1,5})-([А-ЯA-Za-zа-яА-Я0-9]+)-(\\d+)$");

    public List<PublicProtocolResponseDto> protocolsToPublicProtocolsDto(List<Protocol> protocols) {
        List<PublicProtocolResponseDto> publicProtocolResponseDtos = new ArrayList<>();
        for (Protocol protocol : protocols) {
            publicProtocolResponseDtos.add(protocolToPublicProtocolResponseDto(protocol));
        }
        return publicProtocolResponseDtos;
    }

    public PublicProtocolResponseDto protocolToPublicProtocolResponseDto(Protocol protocol) {
        PublicProtocolResponseDto publicProtocolResponseDto = new PublicProtocolResponseDto();

        publicProtocolResponseDto.setId(protocol.getId());
        publicProtocolResponseDto.setProtocolNumber(protocol.getProtocolNumber());

        return publicProtocolResponseDto;
    }

    public List<ProtocolResponseDto> protocolsToProtocolsDto(List<Protocol> protocols) {
        List<ProtocolResponseDto> protocolResponseDtos = new ArrayList<>();
        for (Protocol protocol : protocols) {
            protocolResponseDtos.add(protocolToProtocolResponseDto(protocol));
        }
        return protocolResponseDtos;
    }

    public ProtocolResponseDto protocolToProtocolResponseDto(Protocol protocol) {
        ProtocolResponseDto protocolResponseDto = new ProtocolResponseDto();

        protocolResponseDto.setId(protocol.getId());
        protocolResponseDto.setProtocolNumber(protocol.getProtocolNumber());
        protocolResponseDto.setIssueDate(protocol.getIssueDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        protocolResponseDto.setFullProtocolNumber(protocolResponseDto.getProtocolNumber() + " от " + protocolResponseDto.getIssueDate());
        protocolResponseDto.setClientId(protocol.getClient().getId());

        return protocolResponseDto;
    }

    public Protocol protocolRequestDtoToProtocol(ProtocolRequestDto protocolRequestDto, String sequentialNumber) {
        Protocol protocol = new Protocol();

        protocol.setProtocolNumber(protocolRequestDto.getUniqueNumber().replaceAll(" ", ""));
        protocol.setClient(clientService.getClientById(protocolRequestDto.getClientId()));
        protocol.setIssueDate(LocalDate.parse(protocolRequestDto.getIssueDate(), DateTimeFormatter.ofPattern("dd.MM.yyyy")));

        return protocol;
    }

    public Protocol fieldsToProtocol(Long clientId, String number, String issueDate, String pathFile) {
        Protocol protocol = new Protocol();
        protocol.setClient(clientService.getClientById(clientId));

        protocol.setProtocolNumber(number.replaceAll(" ", ""));

        protocol.setIssueDate(parseDate(issueDate));
        protocol.setFilePath(pathFile);

        return protocol;
    }

    public Map<String, List<ProtocolResponseDto>> groupProtocolsByCipher(List<ProtocolResponseDto> protocols) {
        if (protocols == null || protocols.isEmpty()) {
            return Collections.emptyMap();
        }

        return protocols.stream()
                .filter(protocol -> protocol.getProtocolNumber() != null && !protocol.getProtocolNumber().isEmpty())
                .collect(Collectors.groupingBy(
                        protocol -> extractCipher(protocol.getProtocolNumber())
                ));
    }

    public Map<String, Long> getCipherStatistics(List<ProtocolResponseDto> protocols) {
        if (protocols == null || protocols.isEmpty()) {
            return new HashMap<>();
        }

        return protocols.stream()
                .filter(Objects::nonNull)
                .filter(p -> p.getProtocolNumber() != null && !p.getProtocolNumber().isEmpty())
                .collect(Collectors.groupingBy(
                        p -> extractCipher(p.getProtocolNumber()),
                        Collectors.counting()
                ));
    }

    private String extractCipher(String protocolNumber) {
        int firstDashIndex = protocolNumber.indexOf('-');

        if (firstDashIndex != -1) {
            return protocolNumber.substring(0, firstDashIndex);
        }

        return protocolNumber;
    }

    public LocalDate parseDate(String date) {
        if (date == null) return null;

        DateTimeFormatter f1 = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        DateTimeFormatter f2 = DateTimeFormatter.ofPattern("dd.MM.yy");

        if (date.length() == 10) return LocalDate.parse(date, f1);

        return LocalDate.parse(date, f2);
    }

}
