package controlm.qrcodegenerator.controller;

import controlm.qrcodegenerator.model.Client;
import controlm.qrcodegenerator.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class PublicController {

    private final ClientService clientService;

    @GetMapping("/client/{id}")
    public String publicClientView(@PathVariable Long id, Model model) {
        Client client = clientService.getClientById(id);
        model.addAttribute("client", client);
        return "public/client-view";
    }
}