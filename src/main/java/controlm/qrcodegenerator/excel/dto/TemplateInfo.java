package controlm.qrcodegenerator.excel.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@AllArgsConstructor
public class TemplateInfo {
    private String path;
    private List<FieldMapping> fields;
}
