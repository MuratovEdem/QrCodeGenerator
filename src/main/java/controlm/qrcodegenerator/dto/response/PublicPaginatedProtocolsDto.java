package controlm.qrcodegenerator.dto.response;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class PublicPaginatedProtocolsDto {
    private ClientDto client;
    private List<PublicProtocolResponseDto> protocols;
    private Map<String, List<PublicProtocolResponseDto>> protocolsByCipher;
    private List<String> uniqueCiphers;
    private int countProtocols;
    private int currentPage;
    private int pageSize;
    private int totalPages;
    private String searchQuery;
}
