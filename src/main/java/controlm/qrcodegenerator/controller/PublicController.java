package controlm.qrcodegenerator.controller;

import controlm.qrcodegenerator.dto.response.ClientDto;
import controlm.qrcodegenerator.dto.response.PaginatedProtocolsDto;
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

        PaginatedProtocolsDto paginatedDto = protocolService.getFilteredAndPaginatedDtoForPublic(
                id, search, page, 10);

        Client clientById = clientService.getClientById(id); // TODO перенести в сервис

        ClientDto clientDto = new ClientDto();
        clientDto.setName(clientById.getName());
        clientDto.setId(clientById.getId());
        paginatedDto.setClient(clientDto);

        model.addAttribute("paginatedDto", paginatedDto);

        return "public/client-view";
    }
}