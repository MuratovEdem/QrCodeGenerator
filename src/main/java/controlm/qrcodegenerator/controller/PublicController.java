package controlm.qrcodegenerator.controller;

import controlm.qrcodegenerator.dto.response.PublicClientDto;
import controlm.qrcodegenerator.dto.response.PublicPaginatedProtocolsDto;
import controlm.qrcodegenerator.service.ClientService;
import controlm.qrcodegenerator.service.ProtocolService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/public")
@RequiredArgsConstructor
public class PublicController {

    private final ClientService clientService;
    private final ProtocolService protocolService;

    @GetMapping("/client/{id}")
    public String publicClientView(@PathVariable Long id,
                                   Model model) {

        PublicClientDto clientById = clientService.getPublicClientById(id);

        model.addAttribute("client", clientById);

        return "public/client-view";
    }

    @GetMapping("/client/{id}/protocols")
    @ResponseBody
    public ResponseEntity<PublicPaginatedProtocolsDto> getProtocolsForPublic(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String search) {

        Pageable pageable = PageRequest.of(page, size);
        PublicPaginatedProtocolsDto dto = protocolService.getFilteredAndPaginatedDtoForPublic(id, search, pageable);
        return ResponseEntity.ok(dto);
    }
}