package controlm.qrcodegenerator.dto.response;

import controlm.qrcodegenerator.model.Client;
import controlm.qrcodegenerator.model.Protocol;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class PaginatedProtocolsDto {
    private Client client;
    private List<Protocol> protocols;
    private Map<String, List<Protocol>> protocolsByCipher;
    private List<String> uniqueCiphers;
    private int currentPage;
    private int pageSize;
    private int totalPages;
    private String searchQuery;
}
