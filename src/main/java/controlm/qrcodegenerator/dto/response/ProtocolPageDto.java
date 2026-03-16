package controlm.qrcodegenerator.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class ProtocolPageDto {

    private List<ProtocolResponseDto> protocols;
    private Integer totalPages;
    private Integer currentPage;
}
