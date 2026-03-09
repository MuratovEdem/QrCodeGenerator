package controlm.qrcodegenerator.excel.controller;

import controlm.qrcodegenerator.excel.service.UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequiredArgsConstructor
@RequestMapping("/excel")
public class UploadController {
    private final UploadService uploadService;

    @GetMapping
    public String index() {
        return "excel/upload";
    }

    @PostMapping("/upload")
    public ResponseEntity<byte[]> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            byte[] zipData = uploadService.processUpload(file);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"protocols.zip\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(zipData);
        } catch (Exception e) {
            // В реальном проекте лучше логировать и возвращать более информативный ответ
            return ResponseEntity.badRequest()
                    .body(("Ошибка обработки: " + e.getMessage()).getBytes());
        }
    }
}
