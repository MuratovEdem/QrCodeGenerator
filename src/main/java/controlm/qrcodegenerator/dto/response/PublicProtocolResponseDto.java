package controlm.qrcodegenerator.dto.response;

import lombok.Data;

@Data
public class PublicProtocolResponseDto {

    private Long id;
    private String protocolNumber;

    public String getFullProtocolNumber() {
        return String.format("%s", protocolNumber);
    }
}
