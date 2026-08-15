package controlm.qrcodegenerator.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class PublicPaginatedProtocolsDto {
    private List<PublicProtocolResponseDto> protocols;
    private int currentPage;
    private int totalPages;
    private String searchQuery;
}
