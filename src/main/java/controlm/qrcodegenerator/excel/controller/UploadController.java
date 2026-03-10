package controlm.qrcodegenerator.excel.controller;

import controlm.qrcodegenerator.excel.service.UploadService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
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
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file,
                                        @RequestParam("templateType") String templateType) {
        // Проверка на пустой файл
        if (file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("Файл не выбран или пуст");
        }

        // Проверка расширения файла
        String filename = file.getOriginalFilename();
        if (filename == null || (!filename.endsWith(".xlsx") && !filename.endsWith(".xls"))) {
            return ResponseEntity.badRequest()
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("Пожалуйста, загрузите файл Excel (.xlsx или .xls)");
        }

        if (!"7days".equals(templateType) && !"28days".equals(templateType)) {
            return ResponseEntity.badRequest()
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("Неверный тип шаблона");
        }

        try {
            log.info("Обработка файла: {}", filename);
            byte[] zipData = uploadService.processUpload(file, templateType);

            if (zipData == null || zipData.length == 0) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.TEXT_PLAIN)
                        .body("Сгенерированный архив пуст");
            }

            log.info("ZIP архив создан, размер: {} байт", zipData.length);

            // Возвращаем ZIP файл для скачивания
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"protocols_" + templateType + ".zip\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(zipData.length)
                    .body(zipData);

        } catch (IllegalArgumentException e) {
            log.error("Ошибка в данных файла: ", e);
            return ResponseEntity.badRequest()
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("Ошибка в формате файла: " + e.getMessage());
        } catch (Exception e) {
            log.error("Внутренняя ошибка сервера: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.TEXT_PLAIN)
                    .body("Внутренняя ошибка сервера: " + e.getMessage());
        }
    }
}
