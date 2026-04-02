package controlm.qrcodegenerator.dto.response;

import lombok.Data;

import java.util.Map;
import java.util.Set;

@Data
public class ClientProtocolsViewDto {
    private ClientResponseDto client;
    private Set<String> uniqueCiphers;
    private Map<String, Long> countProtocolsByCipher;
    private long countTotalProtocols;
    private String searchQuery;
}
