package controlm.qrcodegenerator.excel.config;

import controlm.qrcodegenerator.excel.dto.FieldMapping;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class MappingConfig {
    private List<FieldMapping> fields = new ArrayList<>();
}
