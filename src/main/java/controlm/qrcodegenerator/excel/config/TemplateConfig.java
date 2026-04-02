package controlm.qrcodegenerator.excel.config;

import controlm.qrcodegenerator.excel.dto.TemplateMapping;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "protocol")
public class TemplateConfig {
    private List<TemplateMapping> templates = new ArrayList<>();
}
