package controlm.qrcodegenerator.excel.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TemplateSubType {
    private String concreteClass;
    private String path;
    private List<FieldMapping> fields = new ArrayList<>();
}
