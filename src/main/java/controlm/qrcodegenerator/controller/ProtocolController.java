package controlm.qrcodegenerator.controller;

import controlm.qrcodegenerator.dto.response.ProtocolResponseDto;
import controlm.qrcodegenerator.service.ProtocolService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/protocols")
@RequiredArgsConstructor
public class ProtocolController {

    private final ProtocolService protocolService;

//    @PostMapping("/{protocolId}/delete")
//    public String deleteProtocol(@PathVariable Long protocolId) {
//        protocolService.deleteProtocolById(protocolId);
//        return "redirect:/clients/" + clientId;
//    }
}
