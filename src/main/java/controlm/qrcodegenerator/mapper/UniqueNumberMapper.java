package controlm.qrcodegenerator.mapper;

import controlm.qrcodegenerator.dto.request.UniqueNumberRequestDto;
import controlm.qrcodegenerator.dto.response.UniqueNumberResponseDto;
import controlm.qrcodegenerator.model.UniqueNumber;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UniqueNumberMapper {

    public UniqueNumber dtoToUniqueNumber(UniqueNumberRequestDto dto) {
        UniqueNumber uniqueNumber = new UniqueNumber();

        uniqueNumber.setNumber(dto.getNumber());

        return uniqueNumber;
    }

    public List<UniqueNumber> dtosToUniqueNumbers(List<UniqueNumberRequestDto> dtos) {
        if (dtos == null) {
            return new ArrayList<>();
        }

        List<UniqueNumber> uniqueNumbers = new ArrayList<>();

        dtos = cleanUniqueNumbers(dtos);

        for (UniqueNumberRequestDto dto : dtos) {
            uniqueNumbers.add(dtoToUniqueNumber(dto));
        }

        return uniqueNumbers;
    }

    public UniqueNumberResponseDto toResponseDto(UniqueNumber uniqueNumber) {
        UniqueNumberResponseDto uniqueNumberResponseDto = new UniqueNumberResponseDto();

        uniqueNumberResponseDto.setNumber(uniqueNumber.getNumber());

        return uniqueNumberResponseDto;
    }

    public List<UniqueNumberResponseDto> toResponseDtos(List<UniqueNumber> uniqueNumbers) {
        List<UniqueNumberResponseDto> uniqueNumberResponseDtos = new ArrayList<>();

        for (UniqueNumber uniqueNumber : uniqueNumbers) {
            uniqueNumberResponseDtos.add(toResponseDto(uniqueNumber));
        }

        return uniqueNumberResponseDtos;
    }

    private List<UniqueNumberRequestDto> cleanUniqueNumbers(List<UniqueNumberRequestDto> list) {
        return list.stream()
                .filter(s -> s.getNumber() != null)
                .collect(Collectors.toList());
    }
}
