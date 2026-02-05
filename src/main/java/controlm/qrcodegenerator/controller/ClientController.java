package controlm.qrcodegenerator.controller;

import controlm.qrcodegenerator.dto.request.ProtocolRequestDto;
import controlm.qrcodegenerator.dto.response.PaginatedProtocolsDto;
import controlm.qrcodegenerator.dto.response.ProtocolHistoryDto;
import controlm.qrcodegenerator.dto.response.ProtocolPreviewDto;
import controlm.qrcodegenerator.model.Client;
import controlm.qrcodegenerator.service.ClientService;
import controlm.qrcodegenerator.service.PdfProcessingService;
import controlm.qrcodegenerator.service.ProtocolService;
import controlm.qrcodegenerator.service.QRCodeService;
import controlm.qrcodegenerator.service.TempFileStorageService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;
    private final QRCodeService qrCodeService;
    private final ProtocolService protocolService;
    private final PdfProcessingService pdfProcessingService;
    private final TempFileStorageService tempFileStorageService;

    @GetMapping
    public String listClients(@RequestParam(value = "search", required = false) String searchQuery, Model model) {
        List<Client> clients;

        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            clients = clientService.searchClientsByName(searchQuery.trim());
        } else {
            clients = clientService.getAllClients();
        }

        model.addAttribute("protocolService", protocolService);
        model.addAttribute("clients", clients);
        return "clients/list";
    }

    @GetMapping("/create")
    public String createClientForm() {
        return "clients/create-form";
    }

    @GetMapping("/{id}")
    public String viewClient(@PathVariable Long id,
                             @RequestParam(value = "search", required = false) String searchQuery,
                             @RequestParam(value = "page", defaultValue = "0") int page,
                             @RequestParam(value = "size", defaultValue = "20") int pageSize,
                             Model model) {

        PaginatedProtocolsDto paginatedDto = clientService.getClientWithPaginatedProtocols(
                id, searchQuery, page, pageSize);

        model.addAttribute("client", paginatedDto.getClient());
        model.addAttribute("filteredProtocols", paginatedDto.getProtocols());
        model.addAttribute("protocolsByCipher", paginatedDto.getProtocolsByCipher());
        model.addAttribute("uniqueCiphers", paginatedDto.getUniqueCiphers());
        model.addAttribute("currentPage", paginatedDto.getCurrentPage());
        model.addAttribute("pageSize", paginatedDto.getPageSize());
        model.addAttribute("totalPages", paginatedDto.getTotalPages());
        model.addAttribute("searchQuery", paginatedDto.getSearchQuery());

        return "clients/protocols-view";
    }

    @GetMapping("/{id}/create-protocols")
    public String showCreateFrom(@PathVariable Long id, Model model) {
        try {
            Client client = clientService.getClientById(id);

            ProtocolHistoryDto history = protocolService.getProtocolHistoryByClientId(id);

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

    @GetMapping("/{clientId}/save-pdf")
    public String getSavePdfForm(@PathVariable Long clientId,
                                 Model model) {
        model.addAttribute("clientId", clientId);
        return "clients/save-pdf-form";
    }

    @PostMapping("/{clientId}/analyze-pdf")
    public String savePdf(@PathVariable Long clientId,
                          @RequestParam("protocolSize") int protocolSize,
                          @RequestParam("pdfFile") MultipartFile file,
                          RedirectAttributes redirectAttributes,
                          Model model) {

        try {
            File temp = File.createTempFile("upload", "pdf");
            file.transferTo(temp);

            List <ProtocolPreviewDto> result = pdfProcessingService.analyze(temp, protocolSize);
            log.info(result.toString());

            model.addAttribute("protocols", result);
            redirectAttributes.addFlashAttribute("successMessage",
                    String.format("Файл успешно обработан. Создано %d протоколов", result.size()));

        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Ошибка при загрузке файла: " + e.getMessage());
            return "redirect:/clients/" + clientId;
        }

        return "clients/confirm-pdf-form";

        // TODO обработать ошибки и сделать редирект
    }

    @PostMapping("/{clientId}/confirm-pdf")
    public String confirmPdf(@PathVariable Long clientId,
                             @RequestParam("protocolNumbers") String[] numbers,
                             @RequestParam("protocolDates") String[] dates,
                             @RequestParam(value = "fileName", required = false) String[] fileName) {
        List<ProtocolPreviewDto> protocols = new ArrayList<>();

        for (int i = 0; i < numbers.length; i++) {
            ProtocolPreviewDto dto = new ProtocolPreviewDto();
            dto.setNumber(numbers[i]);
            dto.setIssueDate(dates[i]);
            if (fileName != null && i < fileName.length) {
                dto.setFileName(fileName[i]);
            }
            protocols.add(dto);
        }

        try {
            pdfProcessingService.confirm(protocols, clientId);

        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
            return "redirect:/clients/" + clientId;
        }
        return "redirect:/clients/" + clientId;

        // TODO обработать ошибки и сделать редирект
    }

    @PostMapping("/{clientId}/cancel-pdf")
    public String cancelPdf(@PathVariable Long clientId,
                            @RequestParam(value = "fileName", required = false) String[] fileName) throws IOException {
        tempFileStorageService.deleteTempFiles(fileName);

        return "redirect:/clients/" + clientId;
        // TODO обработать ошибки и сделать редирект
    }

    @PostMapping("/{clientId}/protocols/{protocolId}/edit")
    public String updateProtocol(@PathVariable Long clientId,
                                 @PathVariable Long protocolId,
                                 @ModelAttribute ProtocolRequestDto protocolDto) {
        log.info("Updating protocol {} for client {}", protocolId, clientId);
        protocolService.updateProtocol(protocolId, protocolDto);
        return "redirect:/clients/" + clientId;
    }

    @PostMapping("/{clientId}/protocols/{protocolId}/delete")
    public String deleteProtocol(@PathVariable Long clientId,
                                 @PathVariable Long protocolId) {
        log.info("Deleting protocol {} for client {}", protocolId, clientId);
        protocolService.deleteProtocolById(protocolId);
        return "redirect:/clients/" + clientId;
    }

    @GetMapping("/{id}/qr")
    @ResponseBody
    public ResponseEntity<byte[]> getQRCode(@PathVariable Long id) throws Exception {
        byte[] qrCode = qrCodeService.getQRCodeImageBytes(id);
        String fileName = qrCodeService.generateSafeFileName(clientService.getClientById(id));

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + fileName + "\"")
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
