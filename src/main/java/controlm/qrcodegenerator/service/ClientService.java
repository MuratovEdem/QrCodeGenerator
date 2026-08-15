package controlm.qrcodegenerator.service;

import controlm.qrcodegenerator.dto.request.ClientCreateRequestDto;
import controlm.qrcodegenerator.dto.request.ClientFileDto;
import controlm.qrcodegenerator.dto.request.ClientUpdateRequestDto;
import controlm.qrcodegenerator.dto.request.ConstructionSiteRequestDto;
import controlm.qrcodegenerator.dto.request.ContactRequestDto;
import controlm.qrcodegenerator.dto.request.ContractRequestDto;
import controlm.qrcodegenerator.dto.request.UniqueNumberRequestDto;
import controlm.qrcodegenerator.dto.response.ClientResponseDto;
import controlm.qrcodegenerator.dto.response.PublicClientDto;
import controlm.qrcodegenerator.exception.NotFoundException;
import controlm.qrcodegenerator.mapper.ClientMapper;
import controlm.qrcodegenerator.model.Client;
import controlm.qrcodegenerator.model.ClientFile;
import controlm.qrcodegenerator.model.ConstructionSite;
import controlm.qrcodegenerator.model.Contact;
import controlm.qrcodegenerator.model.Contract;
import controlm.qrcodegenerator.model.UniqueNumber;
import controlm.qrcodegenerator.repository.ClientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final FileStorageService fileStorageService;

    public Client getClientById(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Client not found with id: " + id));
    }

    public PublicClientDto getPublicClientById(Long id) {
        Client clientById = getClientById(id);

        return clientMapper.toPublicClientDto(clientById);
    }

    public String getNameById(Long id) {
        return clientRepository.findNameById(id);
    }

    public ClientResponseDto getResponseDtoById(Long clientId) {
        Client client = getClientById(clientId);

        return clientMapper.toResponseDto(client);
    }

    @Transactional(readOnly = true)
    public ClientUpdateRequestDto getUpdateRequestDtoById(Long clientId) {
        Client client = getClientById(clientId);

        return clientMapper.toRequestDto(client);
    }

    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    public Page<PublicClientDto> getPaginatedClients(Pageable pageable) {
        Page<Client> clients = clientRepository.findAll(pageable);
        return clients.map(clientMapper::toPublicClientDto);
    }

    public Resource getFileByClientFileId(Long clientId, Long clientFileId) throws MalformedURLException {
        String filePath = clientRepository.getFilePathByClientFileId(clientId, clientFileId);

        return fileStorageService.loadAsResource(filePath);
    }

    public String getContentTypeByFileId(Long clientId, Long clientFileId) {
        return clientRepository.getContentTypeByFileId(clientId, clientFileId);
    }

    @Transactional
    public Client createClient(ClientCreateRequestDto clientCreateRequestDto) throws IOException {

        Client client = clientMapper.clientCreateRequestDtoToClient(clientCreateRequestDto);


        List<MultipartFile> validFiles = clientCreateRequestDto.getFiles() == null
                ? List.of()
                : clientCreateRequestDto.getFiles().stream()
                .filter(file -> file != null && !file.isEmpty())
                .toList();

        if (!validFiles.isEmpty()) {
            saveFiles(validFiles, client);
        }

        return clientRepository.save(client);
    }

    public Page<PublicClientDto> searchPaginatedClientsByName(String name, Pageable pageable) {
        Page<Client> clients = clientRepository.findByNameIsContainingIgnoreCase(name, pageable);
        return clients.map(clientMapper::toPublicClientDto);
    }

    @Transactional
    public void updateClient(Long clientId, ClientUpdateRequestDto dto) throws IOException {
        Client client = getClientById(clientId);

        client.setName(dto.getName());
        client.setInnKpp(dto.getInnKpp());

        updateContacts(client, dto.getContacts());
        updateConstructionSites(client, dto.getConstructionSites());
        updateContracts(client, dto.getContracts());
        updateUniqueNumbers(client, dto.getUniqueNumbers());
        updateFiles(client, dto.getExistingFiles(), dto.getFiles());

//        clientRepository.save(client);
    }

    public Long generateUniqueNumber() {
        Long maxUniqueNumber = clientRepository.getMaxUniqueNumber();
        if (maxUniqueNumber == null) {
            return 1L;
        }
        return clientRepository.getMaxUniqueNumber() + 1L;
    }

    private void updateFiles(Client client, List<ClientFileDto> retainedFileIds, List<MultipartFile> newFiles) throws IOException {
        List<ClientFile> existingFiles = client.getClientFiles();

        List<ClientFileDto> keepIds = retainedFileIds != null ? retainedFileIds : new ArrayList<>();
        Set<Long> idsToKeep = keepIds.stream()
                .map(ClientFileDto::getId)
                .collect(Collectors.toSet());

        List<ClientFile> filesToDelete = existingFiles.stream()
                .filter(file -> !idsToKeep.contains(file.getId()))
                .toList();

        for (ClientFile file : filesToDelete) {
            fileStorageService.deleteFile(file.getFilePath());
        }

        existingFiles.removeIf(file -> !idsToKeep.contains(file.getId()));

        if (newFiles != null && !newFiles.isEmpty()) {
            saveFiles(newFiles, client);
        }
    }

    private void updateContacts(Client client, List<ContactRequestDto> dtos) {
        List<Contact> existingList = client.getContacts();

        existingList.removeIf(existing -> dtos.stream()
                .noneMatch(dto -> dto.getId() != null && dto.getId().equals(existing.getId())));

        for (ContactRequestDto dto : dtos) {
            if (dto.getId() != null) {
                Contact existing = existingList.stream()
                        .filter(e -> e.getId().equals(dto.getId()))
                        .findFirst().orElseThrow(() -> new NotFoundException("Contact not found"));
                existing.setName(dto.getName());
                existing.setPost(dto.getPost());
                existing.setPhoneNumber(dto.getPhoneNumber());
                existing.setEmail(dto.getEmail());
            } else {
                Contact newEntity = new Contact();
                newEntity.setName(dto.getName());
                newEntity.setPost(dto.getPost());
                newEntity.setPhoneNumber(dto.getPhoneNumber());
                newEntity.setEmail(dto.getEmail());
                newEntity.setClient(client);
                existingList.add(newEntity);
            }
        }
    }

    private void updateConstructionSites(Client client, List<ConstructionSiteRequestDto> dtos) {
        List<ConstructionSite> existingList = client.getConstructionSites();

        existingList.removeIf(existing -> dtos.stream()
                .noneMatch(dto -> dto.getId() != null && dto.getId().equals(existing.getId())));

        for (ConstructionSiteRequestDto dto : dtos) {
            if (dto.getId() != null) {
                ConstructionSite existing = existingList.stream()
                        .filter(e -> e.getId().equals(dto.getId()))
                        .findFirst().orElseThrow(() -> new NotFoundException("ConstructionSite not found"));
                existing.setName(dto.getName());
            } else {
                ConstructionSite newEntity = new ConstructionSite();
                newEntity.setName(dto.getName());
                newEntity.setClient(client);
                existingList.add(newEntity);
            }
        }
    }

    private void updateContracts(Client client, List<ContractRequestDto> dtos) {
        List<Contract> existingList = client.getContracts();

        existingList.removeIf(existing -> dtos.stream()
                .noneMatch(dto -> dto.getId() != null && dto.getId().equals(existing.getId())));

        for (ContractRequestDto dto : dtos) {
            if (dto.getId() != null) {
                Contract existing = existingList.stream()
                        .filter(e -> e.getId().equals(dto.getId()))
                        .findFirst().orElseThrow(() -> new NotFoundException("Contract not found"));
                existing.setName(dto.getName());
            } else {
                Contract newEntity = new Contract();
                newEntity.setName(dto.getName());
                newEntity.setClient(client);
                existingList.add(newEntity);
            }
        }
    }

    private void updateUniqueNumbers(Client client, List<UniqueNumberRequestDto> dtos) {
        List<UniqueNumber> existingList = client.getUniqueNumbers();

        existingList.removeIf(existing -> dtos.stream()
                .noneMatch(dto -> dto.getId() != null && dto.getId().equals(existing.getId())));

        for (UniqueNumberRequestDto dto : dtos) {
            if (dto.getId() != null) {
                UniqueNumber existing = existingList.stream()
                        .filter(e -> e.getId().equals(dto.getId()))
                        .findFirst().orElseThrow(() -> new NotFoundException("UniqueNumber not found"));
                existing.setNumber(dto.getNumber());
            } else {
                UniqueNumber newEntity = new UniqueNumber();
                newEntity.setNumber(dto.getNumber());
                newEntity.setClient(client);
                existingList.add(newEntity);
            }
        }
    }

    private void saveFiles(List<MultipartFile> files, Client client) throws IOException {
        for (MultipartFile file : files) {
            if (file.isEmpty()) continue;
            Path filePath = fileStorageService.saveOriginal(file, client.getName(), "files");

            ClientFile clientFile = new ClientFile();
            clientFile.setFileName(file.getOriginalFilename());
            clientFile.setFilePath(filePath.toString());
            clientFile.setContentType(file.getContentType());
            clientFile.setClient(client);

            client.getClientFiles().add(clientFile);
        }
    }
}
