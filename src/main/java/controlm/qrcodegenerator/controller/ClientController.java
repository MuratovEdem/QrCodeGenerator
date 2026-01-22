package controlm.qrcodegenerator.controller;

import controlm.qrcodegenerator.dto.request.ProtocolRequestDto;
import controlm.qrcodegenerator.dto.response.ProtocolHistoryDto;
import controlm.qrcodegenerator.model.Client;
import controlm.qrcodegenerator.service.ClientService;
import controlm.qrcodegenerator.service.ProtocolService;
import controlm.qrcodegenerator.service.QRCodeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;
    private final QRCodeService qrCodeService;
    private final ProtocolService protocolService;

    @GetMapping
    public String listClients(Model model) {
        List<Client> clients = clientService.getAllClients();
        model.addAttribute("clients", clients);
        return "clients/list";
    }

    @GetMapping("/create")
    public String createClientForm() {
        return "clients/create-form";
    }

    @GetMapping("/{id}")
    public String viewClient(@PathVariable Long id, Model model) {
        Client client = clientService.getClientById(id);
        model.addAttribute("client", client);
        return "clients/protocols-view";
    }

    @GetMapping("/{id}/create-protocols")
    public String showCreateFrom(@PathVariable Long id, Model model) {
        try {
            Client client = clientService.getClientById(id);

            // Получаем историю протоколов для автозаполнения
            ProtocolHistoryDto history = protocolService.getProtocolHistoryByClientId(id);

            // Создаем DTO с предзаполненными значениями
            ProtocolRequestDto formDto = new ProtocolRequestDto();
            formDto.setClientId(id);
            formDto.setCipher(history.getLastCipher());
            formDto.setUniqueNumber(history.getLastUniqueNumber());

            model.addAttribute("client", client);
            model.addAttribute("protocolForm", formDto);
            model.addAttribute("cipherHistory", history.getCipherHistory());
            model.addAttribute("uniqueNumberHistory", history.getUniqueNumberHistory());
            model.addAttribute("clientId", id);
            model.addAttribute("pageTitle", "Добавить протокол для " + client.getName());

            log.info("Отображение формы для клиента ID: {}, история шифров: {}, история номеров: {}",
                    id, history.getCipherHistory().size(), history.getUniqueNumberHistory().size());

            return "protocols/create-protocol";

        } catch (Exception e) {
            log.error("Ошибка при загрузке формы: {}", e.getMessage());
            return "redirect:/clients?error=Ошибка загрузки формы";
        }
    }

    @PostMapping("/{id}/create-protocols")
    public String createProtocolByClientId(@PathVariable Long id,
                                           @Valid @ModelAttribute("protocolForm") ProtocolRequestDto formDto,
                                           BindingResult bindingResult,
                                           Model model,
                                           RedirectAttributes redirectAttributes) {

        try {
            Client client = clientService.getClientById(id);
            formDto.setClientId(id);

            if (bindingResult.hasErrors()) {
                ProtocolHistoryDto history = protocolService.getProtocolHistoryByClientId(id);

                model.addAttribute("client", client);
                model.addAttribute("cipherHistory", history.getCipherHistory());
                model.addAttribute("uniqueNumberHistory", history.getUniqueNumberHistory());
                model.addAttribute("pageTitle", "Добавить протокол для " + client.getName());

                log.warn("Ошибки валидации при сохранении протокола для клиента: {}", id);
                return "protocols/create-protocol";
            }

            protocolService.createProtocols(formDto);
            log.info("Протокол сохранен для клиента ID: {}, шифр: {}, номер: {}",
                    id, formDto.getCipher(), formDto.getUniqueNumber());
            redirectAttributes.addFlashAttribute("successMessage",
                    "Протокол успешно добавлен");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Ошибка при добавлении протокола: " + e.getMessage());
        }

        return "redirect:/clients/" + id + "/create-protocols";
    }

    @GetMapping("/{id}/qr")
    @ResponseBody
    public ResponseEntity<byte[]> getQRCode(@PathVariable Long id) throws Exception {
        byte[] qrCode = qrCodeService.getQRCodeImageBytes(id);
        String name = clientService.getClientById(id).getName();

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + name + "_qr.png\"")
                .contentType(MediaType.IMAGE_PNG)
                .body(qrCode);
    }

    @GetMapping("/{id}/qr/display")
    public String showQRCode(@PathVariable Long id, Model model) throws Exception {
        String qrCodeBase64 = qrCodeService.getQRCodeAsBase64(id);
        model.addAttribute("qrCode", qrCodeBase64);
        model.addAttribute("clientId", id);
        return "clients/qr-display";
    }
}
