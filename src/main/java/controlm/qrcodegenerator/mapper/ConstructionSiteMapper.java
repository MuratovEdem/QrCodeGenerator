package controlm.qrcodegenerator.mapper;

import controlm.qrcodegenerator.dto.request.ConstructionSiteRequestDto;
import controlm.qrcodegenerator.model.ConstructionSite;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ConstructionSiteMapper {

    public ConstructionSite dtoToConstructionSite(ConstructionSiteRequestDto dto) {
        ConstructionSite constructionSite = new ConstructionSite();

        constructionSite.setName(dto.getName());

        return constructionSite;
    }

    public List<ConstructionSite> dtosToConstructionSites(List<ConstructionSiteRequestDto> dtos) {
        if (dtos == null) {
            return new ArrayList<>();
        }

        List<ConstructionSite> constructionSites = new ArrayList<>();

        for (ConstructionSiteRequestDto dto : dtos) {
            constructionSites.add(dtoToConstructionSite(dto));
        }

        return constructionSites;
    }
}
