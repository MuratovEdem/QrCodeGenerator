package controlm.qrcodegenerator.controller;

import controlm.qrcodegenerator.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@RequiredArgsConstructor
@Controller
public class UniqueNumberController {

    ClientService clientService;

    @GetMapping("/generate-unique-number")
    public ResponseEntity<String> generateUniqueNumber() {
        try {
            Long generatedNumber = clientService.generateUniqueNumber();

            return ResponseEntity.ok(String.valueOf(generatedNumber));
        } catch (Exception e) {
            return ResponseEntity
                    .internalServerError()
                    .body("Ошибка при генерации номера: " + e.getMessage());
        }
    }
}
