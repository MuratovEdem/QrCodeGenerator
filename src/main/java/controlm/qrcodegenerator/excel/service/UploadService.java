package controlm.qrcodegenerator.excel.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class UploadService {
    private final ExcelService excelService;
    private final ZipService zipService;

    /**
     * Основной метод обработки: парсинг, генерация протоколов, упаковка в ZIP.
     * Возвращает массив байт готового архива.
     */
    public byte[] processUpload(MultipartFile file, String templateType) throws IOException {
        log.info("Начало обработки файла: {} с типом шаблона: {}",
                file.getOriginalFilename(), templateType);

        Map<File, String> protocolFiles = null;

        try {
            // Генерируем протоколы с нужным типом шаблона
            protocolFiles = excelService.generateProtocols(file, templateType);

            if (protocolFiles.isEmpty()) {
                throw new IOException("Не удалось сгенерировать протоколы");
            }

            log.info("Сгенерировано {} файлов протоколов", protocolFiles.size());

            // Создаем ZIP-архив
            String zipName = "protocols_" + templateType + ".zip";
            byte[] zipData = zipService.createZipWithNames(protocolFiles, zipName);

            if (zipData == null || zipData.length == 0) {
                throw new IOException("Созданный ZIP архив пуст");
            }

            log.info("ZIP архив успешно создан, размер: {} байт", zipData.length);

            return zipData;

        } catch (Exception e) {
            log.error("Ошибка при обработке файла: ", e);
            throw e;
        } finally {
            // Удаляем временные файлы
            if (protocolFiles != null) {
                for (File f : protocolFiles.keySet()) {
                    if (f != null && f.exists()) {
                        boolean deleted = f.delete();
                        if (!deleted) {
                            f.deleteOnExit();
                        }
                    }
                }
            }
        }
    }
}
