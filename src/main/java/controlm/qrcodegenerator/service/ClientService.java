package controlm.qrcodegenerator.service;

import controlm.qrcodegenerator.dto.request.ClientRequestDto;
import controlm.qrcodegenerator.dto.response.PublicClientDto;
import controlm.qrcodegenerator.exception.NotFoundException;
import controlm.qrcodegenerator.mapper.ClientMapper;
import controlm.qrcodegenerator.model.Client;
import controlm.qrcodegenerator.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final ContactService contactService;
    private final ContractService contractService;
    private final UniqueNumberService uniqueNumberService;
    private final ConstructionSiteService constructionSiteService;

    public Client getClientById(Long id){
        return clientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Client not found with id: " + id));
    }

    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    public Page<PublicClientDto> getPaginatedClients(Pageable pageable) {
        Page<Client> clients = clientRepository.findAll(pageable);
        return clients.map(clientMapper::toClientDto);
    }

    @Transactional
    public Client createClient(ClientRequestDto clientRequestDto) {

        Client client = clientMapper.clientRequestDtoToClient(clientRequestDto);

        Client saved = clientRepository.save(client);

        if (!client.getContacts().isEmpty()) {
            contactService.saveListByClient(client.getContacts(), saved);
        }

        if (!client.getContracts().isEmpty()) {
            contractService.saveListByClient(client.getContracts(), saved);
        }

        if (!client.getConstructionSites().isEmpty()) {
            constructionSiteService.saveListByClient(client.getConstructionSites(), saved);
        }

        if (!client.getUniqueNumbers().isEmpty()) {
            uniqueNumberService.saveListByClient(client.getUniqueNumbers(), saved);
        }

        return clientRepository.save(saved);
    }

    public Page<PublicClientDto> searchPaginatedClientsByName(String name, Pageable pageable) {
        Page<Client> clients = clientRepository.findByNameIsContainingIgnoreCase(name, pageable);
        return clients.map(clientMapper::toClientDto);
    }
}
