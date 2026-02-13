package controlm.qrcodegenerator.service;

import controlm.qrcodegenerator.dto.request.ClientRequestDto;
import controlm.qrcodegenerator.dto.response.ClientDto;
import controlm.qrcodegenerator.exception.NotFoundException;
import controlm.qrcodegenerator.mapper.ClientMapper;
import controlm.qrcodegenerator.model.Client;
import controlm.qrcodegenerator.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;

    public Client getClientById(Long id){
        return clientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Client not found with id: " + id));
    }

    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    public Page<ClientDto> getPaginatedClients(Pageable pageable) {
        Page<Client> clients = clientRepository.findAll(pageable);
        return clients.map(clientMapper::toClientDto);
    }

    public Client createClient(ClientRequestDto clientRequestDto) {
        Client client = new Client();
        client.setName(clientRequestDto.getName());
        return clientRepository.save(client);
    }

    public Page<ClientDto> searchPaginatedClientsByName(String name, Pageable pageable) {
        Page<Client> clients = clientRepository.findByNameIsContainingIgnoreCase(name, pageable);
        return clients.map(clientMapper::toClientDto);
    }
}
