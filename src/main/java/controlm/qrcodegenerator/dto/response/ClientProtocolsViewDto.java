package controlm.qrcodegenerator.dto.response;

import lombok.Data;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Data
public class ClientProtocolsViewDto {
    private ClientResponseDto client;
    private Map<String, List<ProtocolResponseDto>> protocolsByCipher;
    private Set<String> uniqueCiphers;
    private Map<String, Long> countProtocolsByCipher;
    private long countTotalProtocols;
    private Map<String, Integer> currentPagesByCipher;
    private Map<String, Integer> totalPagesByCipher;
    private int pageSize;
    private String searchQuery;
    private String currentCipher;
}
