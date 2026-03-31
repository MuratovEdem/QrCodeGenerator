package controlm.qrcodegenerator.mapper;

import controlm.qrcodegenerator.dto.request.ContractRequestDto;
import controlm.qrcodegenerator.dto.response.ContractResponseDto;
import controlm.qrcodegenerator.model.Contract;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

        dtos = cleanContracts(dtos);

        for (ContractRequestDto dto : dtos) {
            contracts.add(dtoToContract(dto));
        }

        return contracts;
    }

    public ContractResponseDto toResponseDto(Contract contract) {
        ContractResponseDto contractResponseDto = new ContractResponseDto();

        contractResponseDto.setName(contract.getName());

        return contractResponseDto;
    }

    public ContractRequestDto toRequestDto(Contract contract) {
        ContractRequestDto contractResponseDto = new ContractRequestDto();

        contractResponseDto.setId(contract.getId());
        contractResponseDto.setName(contract.getName());

        return contractResponseDto;
    }

    public List<ContractResponseDto> toResponseDtos(List<Contract> contracts) {
        List<ContractResponseDto> contractResponseDtos = new ArrayList<>();

        for (Contract contract : contracts) {
            contractResponseDtos.add(toResponseDto(contract));
        }

        return contractResponseDtos;
    }

    public List<ContractRequestDto> toRequestDtos(List<Contract> contracts) {
        List<ContractRequestDto> contractResponseDtos = new ArrayList<>();

        for (Contract contract : contracts) {
            contractResponseDtos.add(toRequestDto(contract));
        }

        return contractResponseDtos;
    }

    private List<ContractRequestDto> cleanContracts(List<ContractRequestDto> list) {
        return list.stream()
                .filter(s -> s.getName() != null
                        && !s.getName().isBlank())
                .collect(Collectors.toList());
    }
}
