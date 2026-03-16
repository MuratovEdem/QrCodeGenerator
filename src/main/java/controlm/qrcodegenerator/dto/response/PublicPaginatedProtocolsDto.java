package controlm.qrcodegenerator.dto.response;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class PublicPaginatedProtocolsDto {
    private PublicClientDto client;
    private List<PublicProtocolResponseDto> protocols;
    private Long countProtocols;
    private int currentPage;
    private int pageSize;
    private int totalPages;
    private String searchQuery;
}
