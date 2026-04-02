package controlm.qrcodegenerator.dto.response;

import lombok.Data;

@Data
public class ProtocolResponseDto {

    private Long id;
    private String cipher;
    private String uniqueNumber;
    private String sequentialNumber;
    private String issueDate;

    public String getFullProtocolNumber() {
        return String.format("%s-%s-%s от %s", cipher, uniqueNumber, sequentialNumber, issueDate);
    }
}
