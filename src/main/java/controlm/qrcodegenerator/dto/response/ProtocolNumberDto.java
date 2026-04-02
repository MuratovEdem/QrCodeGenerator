package controlm.qrcodegenerator.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProtocolNumberDto {
    private String cipher;
    private String uniqueNumber;
    private String sequentialNumber;
}
