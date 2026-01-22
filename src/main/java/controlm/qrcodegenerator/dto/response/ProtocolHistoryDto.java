package controlm.qrcodegenerator.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class ProtocolHistoryDto {
    private String lastCipher;
    private String lastUniqueNumber;
    private List<String> cipherHistory;
    private List<String> uniqueNumberHistory;
}
