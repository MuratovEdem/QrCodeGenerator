package controlm.qrcodegenerator.service;

import controlm.qrcodegenerator.dto.request.ClientRequestDto;
import controlm.qrcodegenerator.dto.response.PaginatedProtocolsDto;
import controlm.qrcodegenerator.exception.NotFoundException;
import controlm.qrcodegenerator.model.Client;
import controlm.qrcodegenerator.model.Protocol;
import controlm.qrcodegenerator.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;

    public Client getClientById(Long id){
        return clientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Client not found with id: " + id));
    }

    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    public Client createClient(ClientRequestDto clientRequestDto) {
        Client client = new Client();
        client.setName(clientRequestDto.getName());
        return clientRepository.save(client);
    }

    public List<Client> searchClientsByName(String name) {
        return clientRepository.findByNameIsContainingIgnoreCase(name);
    }

    public List<Protocol> getFilteredProtocolsByClientId(Long clientId, String filter) {
        Client client = getClientById(clientId);

        List<Protocol> protocols = client.getProtocols();

        if (filter != null && !filter.trim().isEmpty()) {

            String searchTerm = filter.trim().toLowerCase();
            protocols = client.getProtocols().stream()
                    .filter(protocol -> (protocol.getFullProtocolNumber() != null &&
                            protocol.getFullProtocolNumber().toLowerCase().contains(searchTerm))
                    )
                    .toList();
        }

        return protocols;
    }

    public PaginatedProtocolsDto getClientWithPaginatedProtocols(
            Long clientId,
            String filter,
            int page,
            int pageSize) {

        Client client = getClientById(clientId);
        List<Protocol> filteredProtocols = getFilteredProtocolsByClientId(clientId, filter);

        Map<String, List<Protocol>> protocolsByCipher = filteredProtocols.stream()
                .collect(Collectors.groupingBy(
                        Protocol::getCipher,
                        TreeMap::new,
                        Collectors.toList()
                ));

        Map<String, List<Protocol>> paginatedProtocolsByCipher = getPaginatedProtocolsByCipher(page, pageSize, protocolsByCipher);

        // Рассчитываем общее количество страниц
        int maxProtocolsPerCipher = protocolsByCipher.values().stream()
                .mapToInt(List::size)
                .max()
                .orElse(0);

        int totalPages = (int) Math.ceil((double) maxProtocolsPerCipher / pageSize);

        List<String> uniqueCiphers = new ArrayList<>(protocolsByCipher.keySet());

        PaginatedProtocolsDto dto = new PaginatedProtocolsDto();
        dto.setClient(client);
        dto.setProtocols(filteredProtocols);
        dto.setProtocolsByCipher(paginatedProtocolsByCipher);
        dto.setUniqueCiphers(uniqueCiphers);
        dto.setCurrentPage(page);
        dto.setPageSize(pageSize);
        dto.setTotalPages(totalPages);
        dto.setSearchQuery(filter);

        return dto;
    }

    private Map<String, List<Protocol>> getPaginatedProtocolsByCipher(int page, int pageSize, Map<String, List<Protocol>> protocolsByCipher) {
        Map<String, List<Protocol>> paginatedProtocolsByCipher = new TreeMap<>();

        for (Map.Entry<String, List<Protocol>> entry : protocolsByCipher.entrySet()) {
            String cipher = entry.getKey();
            List<Protocol> cipherProtocols = entry.getValue();

            // Разбиваем на страницы по pageSize протоколов на колонку
            int fromIndex = page * pageSize;
            int toIndex = Math.min(fromIndex + pageSize, cipherProtocols.size());

            if (fromIndex < cipherProtocols.size()) {
                List<Protocol> pageProtocols = cipherProtocols.subList(fromIndex, toIndex);
                paginatedProtocolsByCipher.put(cipher, pageProtocols);
            }
        }
        return paginatedProtocolsByCipher;
    }
}
