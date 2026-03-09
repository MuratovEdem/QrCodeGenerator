package controlm.qrcodegenerator.excel.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UploadService {
    private final ExcelService excelService;
    private final ZipService zipService;

    /**
     * Основной метод обработки: парсинг, генерация протоколов, упаковка в ZIP.
     * Возвращает массив байт готового архива.
     */
    public byte[] processUpload(MultipartFile file) throws IOException {
        // 1. Парсим заявку
        List<Map<String, String>> data = excelService.parseRequestFile(file);
        if (data.isEmpty()) {
            throw new IllegalArgumentException("Файл заявки не содержит данных (нет строк после заголовка)");
        }

        // 2. Генерируем временные файлы протоколов
        List<File> protocolFiles = excelService.generateProtocols(data);

        try {
            // 3. Создаём ZIP-архив
            return zipService.createZip(protocolFiles, "protocols.zip");
        } finally {
            // 4. Удаляем временные файлы
            for (File f : protocolFiles) {
                if (f.exists()) {
                    f.delete();
                }
            }
        }
    }
}
