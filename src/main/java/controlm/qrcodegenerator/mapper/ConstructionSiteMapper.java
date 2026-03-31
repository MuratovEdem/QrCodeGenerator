package controlm.qrcodegenerator.mapper;

import controlm.qrcodegenerator.dto.request.ConstructionSiteRequestDto;
import controlm.qrcodegenerator.dto.response.ConstructionSiteResponseDto;
import controlm.qrcodegenerator.model.ConstructionSite;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

        dtos = cleanConstructionSites(dtos);

        for (ConstructionSiteRequestDto dto : dtos) {
            constructionSites.add(dtoToConstructionSite(dto));
        }

        return constructionSites;
    }

    public ConstructionSiteResponseDto toResponseDto(ConstructionSite constructionSite) {
        ConstructionSiteResponseDto constructionSiteResponseDto = new ConstructionSiteResponseDto();

        constructionSiteResponseDto.setName(constructionSite.getName());

        return constructionSiteResponseDto;
    }

    public ConstructionSiteRequestDto toRequestDto(ConstructionSite constructionSite) {
        ConstructionSiteRequestDto constructionSiteResponseDto = new ConstructionSiteRequestDto();

        constructionSiteResponseDto.setId(constructionSite.getId());
        constructionSiteResponseDto.setName(constructionSite.getName());

        return constructionSiteResponseDto;
    }

    public List<ConstructionSiteResponseDto> toResponseDtos(List<ConstructionSite> constructionSites) {
        List<ConstructionSiteResponseDto> constructionSiteResponseDtos = new ArrayList<>();

        for (ConstructionSite constructionSite : constructionSites) {
            constructionSiteResponseDtos.add(toResponseDto(constructionSite));
        }

        return constructionSiteResponseDtos;
    }

    public List<ConstructionSiteRequestDto> toRequestDtos(List<ConstructionSite> constructionSites) {
        List<ConstructionSiteRequestDto> constructionSiteResponseDtos = new ArrayList<>();

        for (ConstructionSite constructionSite : constructionSites) {
            constructionSiteResponseDtos.add(toRequestDto(constructionSite));
        }

        return constructionSiteResponseDtos;
    }

    private List<ConstructionSiteRequestDto> cleanConstructionSites(List<ConstructionSiteRequestDto> list) {
        return list.stream()
                .filter(s -> s.getName() != null
                        && !s.getName().isBlank())
                .collect(Collectors.toList());
    }
}
