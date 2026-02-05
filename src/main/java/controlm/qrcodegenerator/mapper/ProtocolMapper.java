package controlm.qrcodegenerator.mapper;

import controlm.qrcodegenerator.dto.request.ProtocolRequestDto;
import controlm.qrcodegenerator.dto.response.ProtocolNumberDto;
import controlm.qrcodegenerator.dto.response.ProtocolResponseDto;
import controlm.qrcodegenerator.exception.NotFoundException;
import controlm.qrcodegenerator.model.Protocol;
import controlm.qrcodegenerator.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@RequiredArgsConstructor
public class ProtocolMapper {

    private final ClientService clientService;

    private final Pattern PROTOCOL_PATTERN =
            Pattern.compile("^([А-ЯA-Z]{1,5})-([А-ЯA-Za-zа-яА-Я0-9]+)-(\\d+)$");

    public List<ProtocolResponseDto> protocolsToProtocolsDto(List<Protocol> protocols) {
        List<ProtocolResponseDto> protocolResponseDtos = new ArrayList<>();
        for (Protocol protocol : protocols) {
            ProtocolResponseDto protocolResponseDto = new ProtocolResponseDto();
            protocolResponseDto.setName(protocol.getFullProtocolNumber());
            protocolResponseDtos.add(protocolResponseDto);
        }
        return protocolResponseDtos;
    }

    public Protocol protocolRequestDtoToProtocol(ProtocolRequestDto protocolRequestDto, Long sequentialNumber) {
        Protocol protocol = new Protocol();
        protocol.setCipher(protocolRequestDto.getCipher());
        protocol.setUniqueNumber(protocolRequestDto.getUniqueNumber());
        protocol.setSequentialNumber(sequentialNumber);
        protocol.setClient(clientService.getClientById(protocolRequestDto.getClientId()));

        return protocol;
    }

    public Protocol fieldsToProtocol(Long clientId, String number, String issueDate, String pathFile) {
        ProtocolNumberDto protocolNumber = parseNumberToNumberDto(number)
                .orElseThrow(() -> new NotFoundException("Parse protocol number exception"));

        Protocol protocol = new Protocol();
        protocol.setClient(clientService.getClientById(clientId));

        protocol.setCipher(protocolNumber.getCipher());
        protocol.setUniqueNumber(protocolNumber.getUniqueNumber());
        protocol.setSequentialNumber(protocolNumber.getSequentialNumber());

        protocol.setIssueDate(parseDate(issueDate));
        protocol.setFilePath(pathFile);

        return protocol;
    }


    public Optional<ProtocolNumberDto> parseNumberToNumberDto(String number) {
        if (number == null || number.trim().isEmpty()) {
            return Optional.empty();
        }

        Matcher matcher = PROTOCOL_PATTERN.matcher(number.trim());
        if (matcher.matches()) {
            try {
                String code = matcher.group(1);
                String uniqueNumber = matcher.group(2);
                Long serialNumber = Long.parseLong(matcher.group(3));

                return Optional.of(new ProtocolNumberDto(code, uniqueNumber, serialNumber));
            } catch (NumberFormatException e) {
                return Optional.empty();
            }
        }

        return Optional.empty();
    }

    private LocalDate parseDate(String date) {
        if (date == null) return null;

        DateTimeFormatter f1 = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        DateTimeFormatter f2 = DateTimeFormatter.ofPattern("dd.MM.yy");

        if (date.length() == 10) return LocalDate.parse(date, f1);

        return LocalDate.parse(date, f2);
    }

}
