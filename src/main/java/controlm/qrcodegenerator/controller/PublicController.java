package controlm.qrcodegenerator.controller;

import controlm.qrcodegenerator.dto.response.PaginatedProtocolsDto;
import controlm.qrcodegenerator.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;


@Controller
@RequestMapping("/public")
@RequiredArgsConstructor
public class PublicController {

    private final ClientService clientService;

    @GetMapping("/client/{id}")
    public String publicClientView(@PathVariable Long id,
                                   @RequestParam(required = false) String search,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "10") int size,
                                   Model model) {

        PaginatedProtocolsDto paginatedDto = clientService.getClientWithPaginatedProtocols(
                id, search, page, size);

        model.addAttribute("client", paginatedDto.getClient());
        model.addAttribute("totalProtocols", paginatedDto.getClient().getProtocols().size());
        model.addAttribute("currentPage", paginatedDto.getCurrentPage());
        model.addAttribute("searchQuery", paginatedDto.getSearchQuery());
        model.addAttribute("pageSize", paginatedDto.getPageSize());
        model.addAttribute("filteredProtocols", paginatedDto.getProtocols());
        model.addAttribute("uniqueCiphers", paginatedDto.getUniqueCiphers());
        model.addAttribute("protocolsByCipher", paginatedDto.getProtocolsByCipher());
        model.addAttribute("totalPages", paginatedDto.getTotalPages());

        return "public/client-view";
    }

    @GetMapping("/client1/{id}")
    public String publicClient1View(@PathVariable Long id,
                                   @RequestParam(required = false) String search,
                                   @RequestParam(defaultValue = "0") int page,
                                   @RequestParam(defaultValue = "10") int size,
                                   Model model) {

        PaginatedProtocolsDto paginatedDto = clientService.getClientWithPaginatedProtocols(
                id, search, page, size);

        model.addAttribute("client", paginatedDto.getClient());
        model.addAttribute("totalProtocols", paginatedDto.getClient().getProtocols().size());
        model.addAttribute("currentPage", paginatedDto.getCurrentPage());
        model.addAttribute("searchQuery", paginatedDto.getSearchQuery());
        model.addAttribute("pageSize", paginatedDto.getPageSize());
        model.addAttribute("filteredProtocols", paginatedDto.getProtocols());
        model.addAttribute("uniqueCiphers", paginatedDto.getUniqueCiphers());
        model.addAttribute("protocolsByCipher", paginatedDto.getProtocolsByCipher());
        model.addAttribute("totalPages", paginatedDto.getTotalPages());

        return "public/client1-view";
    }

    @GetMapping("/client2/{id}")
    public String publicClient2View(@PathVariable Long id,
                                    @RequestParam(required = false) String search,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "10") int size,
                                    Model model) {

        PaginatedProtocolsDto paginatedDto = clientService.getClientWithPaginatedProtocols(
                id, search, page, size);

        model.addAttribute("client", paginatedDto.getClient());
        model.addAttribute("totalProtocols", paginatedDto.getClient().getProtocols().size());
        model.addAttribute("currentPage", paginatedDto.getCurrentPage());
        model.addAttribute("searchQuery", paginatedDto.getSearchQuery());
        model.addAttribute("pageSize", paginatedDto.getPageSize());
        model.addAttribute("filteredProtocols", paginatedDto.getProtocols());
        model.addAttribute("uniqueCiphers", paginatedDto.getUniqueCiphers());
        model.addAttribute("protocolsByCipher", paginatedDto.getProtocolsByCipher());
        model.addAttribute("totalPages", paginatedDto.getTotalPages());

        return "public/client2-view";
    }

    @GetMapping("/client3/{id}")
    public String publicClient3View(@PathVariable Long id,
                                    @RequestParam(required = false) String search,
                                    @RequestParam(defaultValue = "0") int page,
                                    @RequestParam(defaultValue = "10") int size,
                                    Model model) {

        PaginatedProtocolsDto paginatedDto = clientService.getClientWithPaginatedProtocols(
                id, search, page, size);

        model.addAttribute("client", paginatedDto.getClient());
        model.addAttribute("currentDate", LocalDate.now());

        model.addAttribute("totalProtocols", paginatedDto.getClient().getProtocols().size());
        model.addAttribute("currentPage", paginatedDto.getCurrentPage());
        model.addAttribute("searchQuery", paginatedDto.getSearchQuery());
        model.addAttribute("pageSize", paginatedDto.getPageSize());
        model.addAttribute("filteredProtocols", paginatedDto.getProtocols());
        model.addAttribute("uniqueCiphers", paginatedDto.getUniqueCiphers());
        model.addAttribute("protocolsByCipher", paginatedDto.getProtocolsByCipher());
        model.addAttribute("totalPages", paginatedDto.getTotalPages());

        return "public/client3-view";
    }
}