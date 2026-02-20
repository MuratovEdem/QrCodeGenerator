package controlm.qrcodegenerator.service;

import controlm.qrcodegenerator.model.Client;
import controlm.qrcodegenerator.model.ConstructionSite;
import controlm.qrcodegenerator.repository.ConstructionSiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ConstructionSiteService {

    private final ConstructionSiteRepository constructionSiteRepository;

    public void saveListByClient(List<ConstructionSite> constructionSiteList, Client client) {
        for (ConstructionSite constructionSite : constructionSiteList) {
            constructionSite.setClient(client);
            constructionSiteRepository.save(constructionSite);
        }
    }
}
