package controlm.qrcodegenerator.dto.response;

import lombok.Data;

@Data
public class PublicProtocolResponseDto {

    private Long id;
    private String cipher;
    private String uniqueNumber;
    private String sequentialNumber;

    public String getFullProtocolNumber() {
        return String.format("%s-%s-%s", cipher, uniqueNumber, sequentialNumber);
    }
}
