package controlm.qrcodegenerator.controller;

import controlm.qrcodegenerator.model.User;
import controlm.qrcodegenerator.service.ClientService;
import controlm.qrcodegenerator.service.ProtocolService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@Slf4j
public class DashboardController {
    private final ClientService clientService;
    private final ProtocolService protocolService;

    @GetMapping
    public String dashboard(@AuthenticationPrincipal User currentUser, Model model) {
        model.addAttribute("user", currentUser);

//        // Статистика для дашборда
//        long clientCount = clientService.count();
//        long protocolCount = protocolService.count();
//
//        model.addAttribute("clientCount", clientCount);
//        model.addAttribute("protocolCount", protocolCount);
//        model.addAttribute("recentClients", clientService.findRecent(5));

        return "dashboard";
    }
}
