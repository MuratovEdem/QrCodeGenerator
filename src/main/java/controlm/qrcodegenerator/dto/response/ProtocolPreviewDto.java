package controlm.qrcodegenerator.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProtocolPreviewDto {
    private String fileName;
    private String number;
    private String issueDate;
}
