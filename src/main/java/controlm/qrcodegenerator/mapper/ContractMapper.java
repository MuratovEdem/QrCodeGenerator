package controlm.qrcodegenerator.mapper;

import controlm.qrcodegenerator.dto.request.ContractRequestDto;
import controlm.qrcodegenerator.model.Contract;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ContractMapper {

    public Contract dtoToContract(ContractRequestDto dto) {
        Contract contract = new Contract();

        contract.setName(dto.getName());

        return contract;
    }

    public List<Contract> dtosToContracts(List<ContractRequestDto> dtos) {
        if (dtos == null) {
            return new ArrayList<>();
        }

        List<Contract> contracts = new ArrayList<>();

        for (ContractRequestDto dto : dtos) {
            contracts.add(dtoToContract(dto));
        }

        return contracts;
    }
}
