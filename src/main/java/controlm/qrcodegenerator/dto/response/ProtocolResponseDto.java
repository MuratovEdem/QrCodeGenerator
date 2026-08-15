package controlm.qrcodegenerator.dto.response;

import lombok.Data;

@Data
public class ProtocolResponseDto {

    private Long id;
    private String protocolNumber;
    private String issueDate;
    private String fullProtocolNumber;
    private Long clientId;

    private String createdInfo;
    private String updatedInfo;
}
