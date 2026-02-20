package controlm.qrcodegenerator.controller;

import controlm.qrcodegenerator.dto.request.ClientRequestDto;
import controlm.qrcodegenerator.dto.request.ProtocolRequestDto;
import controlm.qrcodegenerator.dto.response.PublicClientDto;
import controlm.qrcodegenerator.dto.response.ClientProtocolsViewDto;
import controlm.qrcodegenerator.mapper.ClientMapper;
import controlm.qrcodegenerator.model.Client;
import controlm.qrcodegenerator.model.OcrJob;
import controlm.qrcodegenerator.service.ClientService;
import controlm.qrcodegenerator.service.FileStorageService;
import controlm.qrcodegenerator.service.OcrAsyncService;
import controlm.qrcodegenerator.service.OcrJobService;
import controlm.qrcodegenerator.service.ProtocolService;
import controlm.qrcodegenerator.service.QRCodeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

import java.io.IOException;
import java.nio.file.Path;
import java.security.Principal;

@Slf4j
@Controller
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;
    private final QRCodeService qrCodeService;
    private final ProtocolService protocolService;
    private final FileStorageService fileStorageService;
    private final OcrAsyncService ocrAsyncService;
    private final OcrJobService ocrJobService;
    private final ClientMapper clientMapper;

    @GetMapping
    public String listClients(@RequestParam(value = "search", required = false) String searchQuery,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "10") int size,
                              Model model) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        Page<PublicClientDto> clients;

        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            clients = clientService.searchPaginatedClientsByName(searchQuery.trim(), pageable);
        } else {
            clients = clientService.getPaginatedClients(pageable);
        }

        model.addAttribute("protocolService", protocolService);
        model.addAttribute("page", clients);
        model.addAttribute("clients", clients.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);

        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            model.addAttribute("searchQuery", searchQuery.trim());
        }
        return "clients/list";
    }

    @GetMapping("/create")
    public String createClientForm(Model model) {
        model.addAttribute("clientRequestDto", new ClientRequestDto());
        return "clients/create-form";
    }

    @PostMapping("/create")
    public String createClient(@ModelAttribute ClientRequestDto clientRequestDto, RedirectAttributes redirectAttributes) {
        try {
            clientService.createClient(clientRequestDto);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Клиент успешно создан");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Ошибка при создании клиента: " + e.getMessage());
        }

        return "redirect:/clients/create";
    }

    @GetMapping("/{id}")
    public String viewClient(@PathVariable Long id,
                             @RequestParam(value = "search", required = false) String searchQuery,
                             @RequestParam(required = false, value = "page", defaultValue = "0") int page,
                             @RequestParam(required = false, value = "size", defaultValue = "20") int pageSize,
                             Model model) {

        Pageable pageable = PageRequest.of(
                page,
                pageSize
        );

        ClientProtocolsViewDto paginatedDto = protocolService.findAllByClientIdWithFilter(id, searchQuery, pageable);

        Client clientById = clientService.getClientById(id); // TODO перенести в сервис

        PublicClientDto publicClientDto = new PublicClientDto();
        publicClientDto.setName(clientById.getName());
        publicClientDto.setId(clientById.getId());
        paginatedDto.setClient(publicClientDto);

        model.addAttribute("paginatedDto", paginatedDto);

        return "clients/protocols-view";
    }

    @GetMapping("/{id}/create-protocols")
    public String showCreateFrom(@PathVariable Long id, Model model) {
        try {
            PublicClientDto client = clientMapper.toClientDto(clientService.getClientById(id));

            ProtocolRequestDto formDto = new ProtocolRequestDto();

            model.addAttribute("client", client);
            model.addAttribute("protocolForm", formDto);
            model.addAttribute("clientId", id);
            model.addAttribute("pageTitle", "Добавить протокол для " + client.getName());

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

                model.addAttribute("client", client);
                model.addAttribute("pageTitle", "Добавить протокол для " + client.getName());

                log.warn("Ошибки валидации при сохранении протокола для клиента: {}", id);
                return "protocols/create-protocol";
            }

            protocolService.createProtocol(formDto);

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
        return "clients/upload-pdf-form";
    }

    @PostMapping("/{clientId}/upload")
    public String upload(@PathVariable Long clientId,
                         @RequestParam("pdfFile") MultipartFile file,
                         Principal principal,
                         Model model) throws IOException {

        Path path = fileStorageService.saveOriginal(file, clientId);
        OcrJob job = ocrJobService.create(clientId,
                principal.getName(),
                path.toString());

        ocrAsyncService.start(job.getId());

        model.addAttribute("ocrJob", job);
        model.addAttribute("clientName", clientService.getClientById(clientId).getName());

        return "redirect:/ocr/jobs" ;
    } // TODO обработать ошибки

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
