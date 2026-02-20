package controlm.qrcodegenerator.mapper;

import controlm.qrcodegenerator.dto.request.UniqueNumberRequestDto;
import controlm.qrcodegenerator.model.UniqueNumber;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

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

        for (UniqueNumberRequestDto dto : dtos) {
            uniqueNumbers.add(dtoToUniqueNumber(dto));
        }

        return uniqueNumbers;
    }
}
