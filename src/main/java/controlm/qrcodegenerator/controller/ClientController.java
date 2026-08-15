package controlm.qrcodegenerator.controller;

import controlm.qrcodegenerator.dto.request.ClientCreateRequestDto;
import controlm.qrcodegenerator.dto.request.ClientUpdateRequestDto;
import controlm.qrcodegenerator.dto.request.ProtocolRequestDto;
import controlm.qrcodegenerator.dto.request.ProtocolUpdateDto;
import controlm.qrcodegenerator.dto.response.ClientProtocolsViewDto;
import controlm.qrcodegenerator.dto.response.FailedFileDto;
import controlm.qrcodegenerator.dto.response.ProtocolPageDto;
import controlm.qrcodegenerator.dto.response.PublicClientDto;
import controlm.qrcodegenerator.mapper.ClientMapper;
import controlm.qrcodegenerator.model.Client;
import controlm.qrcodegenerator.model.FailedFile;
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
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
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
import java.net.MalformedURLException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.List;
import java.util.Objects;

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
        model.addAttribute("clientRequestDto", new ClientCreateRequestDto());
        return "clients/create-form";
    }

    @PostMapping("/create")
    public String createClient(@Valid @ModelAttribute("clientRequestDto") ClientCreateRequestDto createRequestDto,
                               RedirectAttributes redirectAttributes,
                               Model model,
                               BindingResult result) {
        if (result.hasErrors()) {
            return "clients/create-form";
        }

        try {
            clientService.createClient(createRequestDto);
            redirectAttributes.addFlashAttribute("successMessage",
                    "Клиент успешно создан");

            return "redirect:/clients/create";

        } catch (IllegalArgumentException | IOException e ) {
            model.addAttribute("clientRequestDto", createRequestDto);
            model.addAttribute("errorMessage", e.getMessage());
            return "clients/create-form";
        }  catch (Exception e) {
            model.addAttribute("clientRequestDto", createRequestDto);
            model.addAttribute("errorMessage", "Ошибка при создании клиента");
            return "clients/create-form";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        ClientUpdateRequestDto clientUpdateRequestDto = clientService.getUpdateRequestDtoById(id);

        model.addAttribute("clientId", id);
        model.addAttribute("clientRequestDto", clientUpdateRequestDto);
        return "clients/edit-form";
    }

    @PostMapping("/edit/{id}")
    public String updateClient(@PathVariable Long id,
                               @Valid @ModelAttribute("clientRequestDto") ClientUpdateRequestDto clientUpdateRequestDto,
                               BindingResult result,
                               RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Пожалуйста, исправьте ошибки в форме");
            return "redirect:/clients/edit/" + id;
        }

        try {
            clientService.updateClient(id, clientUpdateRequestDto);

            redirectAttributes.addFlashAttribute("successMessage",
                    "Клиент успешно обновлен");
            return "redirect:/clients/" + id;
        } catch (Exception e) {
            log.error("Ошибка при обновлении клиента", e);
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Ошибка при обновлении клиента: " + e.getMessage());
            return "redirect:/clients/edit/" + id;
        }
    }

    @GetMapping("/{id}")
    public String viewClient(@PathVariable Long id,
                             @RequestParam(required = false) String search,
                             Model model) {

        ClientProtocolsViewDto paginatedDto = protocolService
                .findAllByClientIdWithFilter(id, search);

        paginatedDto.setClient(clientService.getResponseDtoById(id));
        model.addAttribute("paginatedDto", paginatedDto);

        return "clients/protocols-view";
    }

    @GetMapping("/{clientId}/file/{clientFileId}")
    public ResponseEntity<Resource> downloadProtocol(@PathVariable Long clientId,
                                                     @PathVariable Long clientFileId) throws MalformedURLException {
        Resource resource = clientService.getFileByClientFileId(clientId, clientFileId);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(clientService.getContentTypeByFileId(clientId, clientFileId)))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @GetMapping("/{clientId}/protocols")
    @ResponseBody
    public ProtocolPageDto getProtocolsByCipher(
            @PathVariable Long clientId,
            @RequestParam String cipher,
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam(required = false) String search,
            Authentication authentication) {

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(a -> Objects.equals(a.getAuthority(), "ROLE_ADMIN"));

        Pageable pageable = PageRequest.of(page, size);

        return protocolService.getProtocolsByCipher(clientId, search, cipher, pageable, isAdmin);
    }

    @GetMapping("/{id}/create-protocols")
    public String showCreateFrom(@PathVariable Long id, Model model) {
        try {
            PublicClientDto client = clientMapper.toPublicClientDto(clientService.getClientById(id));

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
                         Model model) {

        try {
            Path path = fileStorageService.saveOriginal(file, clientService.getNameById(clientId), "original");
            OcrJob job = ocrJobService.create(clientId, principal.getName(), path.toString());
            ocrAsyncService.start(job.getId());

            model.addAttribute("successMessage",
                    "Файл успешно загружен и поставлен в очередь на обработку");
        } catch (Exception e) {
            model.addAttribute("errorMessage",
                    "Ошибка при загрузке файла: " + e.getMessage());
        }

        model.addAttribute("clientId", clientId);
        return "clients/upload-pdf-form";
    }

    @PostMapping("/{clientId}/protocols/{protocolId}/edit")
    public ResponseEntity<Void> updateProtocol(@PathVariable Long clientId,
                                 @PathVariable Long protocolId,
                                 @ModelAttribute ProtocolUpdateDto protocolDto) throws IOException {

        protocolService.updateProtocol(protocolId, protocolDto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{clientId}/protocols/{protocolId}/delete")
    public ResponseEntity<Void> deleteProtocol(@PathVariable Long clientId,
                                 @PathVariable Long protocolId) throws IOException {

        protocolService.deleteProtocolById(protocolId);
        return ResponseEntity.ok().build();
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

    @GetMapping("/{id}/failed-files")
    @ResponseBody
    public List<FailedFileDto> getFailedFiles(@PathVariable Long id) {
        return clientService.getFailedFiles(id);
    }

    @PostMapping("/{id}/failed-files")
    @ResponseBody
    public ResponseEntity<FailedFileDto> uploadFailedFile(@PathVariable Long id,
                                                          @RequestParam("file") MultipartFile file) {
        try {
            return ResponseEntity.ok(clientService.uploadFailedFile(id, file));
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{clientId}/failed-files/{fileId}")
    public ResponseEntity<Resource> viewFile(@PathVariable Long clientId,
                                             @PathVariable Long fileId) {
        FailedFile file = clientService.findFailedFileById(fileId);

        // Проверка, что файл принадлежит данному клиенту
        if (!file.getClient().getId().equals(clientId)) {
            return ResponseEntity.notFound().build();
        }

        try {
            Path path = Paths.get(file.getFilePath());
            Resource resource = new UrlResource(path.toUri());

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(file.getContentType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            ContentDisposition.inline()
                                    .filename(file.getFileName(), StandardCharsets.UTF_8)
                                    .build()
                                    .toString())
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
