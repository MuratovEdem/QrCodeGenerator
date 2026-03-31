package controlm.qrcodegenerator.dto.response;

import lombok.Data;

import java.util.List;

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
