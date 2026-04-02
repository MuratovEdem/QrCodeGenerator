package controlm.qrcodegenerator.service;

import controlm.qrcodegenerator.model.Client;
import controlm.qrcodegenerator.model.Contract;
import controlm.qrcodegenerator.repository.ContractRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ContractService {

    private final ContractRepository contractRepository;

    public void saveListByClient(List<Contract> contracts, Client client) {
        for (Contract contract : contracts) {
            contract.setClient(client);
            contractRepository.save(contract);
        }
    }

}
