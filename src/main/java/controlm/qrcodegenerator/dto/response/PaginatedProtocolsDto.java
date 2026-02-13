package controlm.qrcodegenerator.dto.response;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class PaginatedProtocolsDto {
    private ClientDto client;
    private List<ProtocolResponseDto> protocols;
    private Map<String, List<ProtocolResponseDto>> protocolsByCipher;
    private List<String> uniqueCiphers;
    private int countProtocols;
    private int currentPage;
    private int pageSize;
    private int totalPages;
    private String searchQuery;
}
