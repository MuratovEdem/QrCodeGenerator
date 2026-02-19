package controlm.qrcodegenerator.dto.response;

import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
public class ClientProtocolsViewDto {
    private PublicClientDto client;
    private Map<String, List<ProtocolResponseDto>> protocolsByCipher;
    private Set<String> uniqueCiphers;
    private Map<String, Long> countProtocolsByCipher;
    private long countTotalProtocols;
    private int currentPage;
    private int pageSize;
    private int totalPages;
    private String searchQuery;
}
