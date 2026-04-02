package controlm.qrcodegenerator.excel.dto;

import lombok.Data;

@Data
public class FieldMapping {
    private String sourceColumnName;
    private String sheetName;
    private String cellReference;
}
