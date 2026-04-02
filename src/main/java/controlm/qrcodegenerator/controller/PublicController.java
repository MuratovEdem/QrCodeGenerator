package controlm.qrcodegenerator.controller;

import controlm.qrcodegenerator.dto.response.PublicClientDto;
import controlm.qrcodegenerator.dto.response.PublicPaginatedProtocolsDto;
import controlm.qrcodegenerator.model.Client;
import controlm.qrcodegenerator.service.ClientService;
import controlm.qrcodegenerator.service.ProtocolService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/public")
@RequiredArgsConstructor
public class PublicController {

    private final ClientService clientService;
    private final ProtocolService protocolService;

    @GetMapping("/client/{id}")
    public String publicClientView(@PathVariable Long id,
                                   @RequestParam(required = false) String search,
                                   @RequestParam(required = false, defaultValue = "0") int page,
                                   Model model) {

        PublicPaginatedProtocolsDto paginatedDto = protocolService.getFilteredAndPaginatedDtoForPublic(
                id, search, page, 10);

        Client clientById = clientService.getClientById(id); // TODO перенести в сервис

        PublicClientDto publicClientDto = new PublicClientDto();
        publicClientDto.setName(clientById.getName());
        publicClientDto.setId(clientById.getId());
        paginatedDto.setClient(publicClientDto);

        model.addAttribute("paginatedDto", paginatedDto);

        return "public/client-view";
    }
}