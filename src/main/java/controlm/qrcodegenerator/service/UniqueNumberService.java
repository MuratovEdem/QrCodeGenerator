package controlm.qrcodegenerator.service;

import controlm.qrcodegenerator.model.Client;
import controlm.qrcodegenerator.model.UniqueNumber;
import controlm.qrcodegenerator.repository.UniqueNUmberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UniqueNumberService {

    private final UniqueNUmberRepository uniqueNUmberRepository;

    public void saveListByClient(List<UniqueNumber> uniqueNumbers, Client client) {
        for (UniqueNumber uniqueNumber : uniqueNumbers) {
            uniqueNumber.setClient(client);
            uniqueNUmberRepository.save(uniqueNumber);
        }
    }
}
