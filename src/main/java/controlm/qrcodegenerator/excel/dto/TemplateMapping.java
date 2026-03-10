package controlm.qrcodegenerator.excel.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TemplateMapping {
    private String type;
    private String name;
    private String path;
    private List<FieldMapping> fields = new ArrayList<>();
    private List<TemplateSubType> subTypes = new ArrayList<>();

    public boolean hasSubTypes() {
        return subTypes != null && !subTypes.isEmpty();
    }
}
