package controlm.qrcodegenerator.controller;

import controlm.qrcodegenerator.dto.response.ProtocolResponseDto;
import controlm.qrcodegenerator.service.ProtocolService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Controller
@RequestMapping("/protocols")
@RequiredArgsConstructor
public class ProtocolController {

    private final ProtocolService protocolService;

//    @GetMapping("/{id}")
//    public String findById(@PathVariable Long id) {
//        protocolService.
//        return ResponseEntity.status(HttpStatus.OK).body(studentService.findByIdStudent(id));
//    }

    @GetMapping("/{id}")
    public String findAllByClient(@PathVariable Long id, Model model) {
        List<ProtocolResponseDto> protocols = protocolService.findByClientId(id);

        model.addAttribute("protocols", protocols);

        return "protocols/view";
    }
}
