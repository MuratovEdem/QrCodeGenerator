package controlm.qrcodegenerator.service;

import controlm.qrcodegenerator.model.Client;
import controlm.qrcodegenerator.model.UniqueNumber;
import controlm.qrcodegenerator.repository.UniqueNumberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UniqueNumberService {

    private final UniqueNumberRepository uniqueNumberRepository;

    public void saveListByClient(List<UniqueNumber> uniqueNumbers, Client client) {
        validateUniqueNumbers(uniqueNumbers);

        for (UniqueNumber uniqueNumber : uniqueNumbers) {
            uniqueNumber.setClient(client);
            uniqueNumberRepository.save(uniqueNumber);
        }
    }

    public Long generateUniqueNumber() {
        Long uniqueNumber = uniqueNumberRepository.findMaxNumber() + 1;

        while (uniqueNumberRepository.existsByNumber(uniqueNumber)) {
            uniqueNumber++;
        }

        return uniqueNumber;
    }

    private void validateUniqueNumbers(List<UniqueNumber> uniqueNumbers) {
        for (UniqueNumber uniqueNumber : uniqueNumbers) {
            if (uniqueNumberRepository.existsByNumber(uniqueNumber.getNumber())) {
                throw new IllegalArgumentException("Номер уже существует: " + uniqueNumber.getNumber());
            }
        }
    }
}
