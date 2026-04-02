package controlm.qrcodegenerator.excel.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
@Service
public class ZipService {

    /**
     * Создаёт ZIP-архив из списка файлов и возвращает его в виде массива байт.
     */
    public byte[] createZip(List<File> files, String zipName, List<Map<String, String>> data) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            for (int i = 0; i < files.size(); i++) {
                File file = files.get(i);
                if (!file.exists() || file.length() == 0) {
                    log.warn("Файл {} не существует или пуст, пропускаем", file.getName());
                    continue;
                }

                Map<String, String> rowData = data.get(i);

                String entryName = rowData.get("Номер протокола");
                entryName = entryName.substring(11, entryName.length() - 14) + ".xlsx";


                ZipEntry entry = new ZipEntry(entryName);
                zos.putNextEntry(entry);

                try (FileInputStream fis = new FileInputStream(file)) {
                    byte[] buffer = new byte[8192];
                    int length;
                    while ((length = fis.read(buffer)) > 0) {
                        zos.write(buffer, 0, length);
                    }
                }

                zos.closeEntry();
                log.debug("Добавлен файл в архив: {}", entryName);
            }

            zos.finish();
            zos.flush();
        } catch (Exception e) {
            log.error("Ошибка при создании ZIP архива", e);
            throw new IOException("Ошибка при создании ZIP архива", e);
        }

        byte[] result = baos.toByteArray();
        log.info("ZIP архив создан, размер: {} байт", result.length);

        if (result.length == 0) {
            throw new IOException("Созданный ZIP архив пуст");
        }

        return result;
    }

    public byte[] createZipWithNames(Map<File, String> files, String zipName) throws IOException {
        if (files == null || files.isEmpty()) {
            throw new IllegalArgumentException("Нет файлов для архивации");
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try (ZipOutputStream zos = new ZipOutputStream(baos)) {
            // Проходим по всем файлам
            for (Map.Entry<File, String> entry : files.entrySet()) {
                File file = entry.getKey();
                String targetFileName = entry.getValue();
                targetFileName = targetFileName.substring(11, targetFileName.length() - 19) + ".xlsx";


                // Проверяем существование файла
                if (!file.exists()) {
                    log.warn("Файл {} не существует, пропускаем", file.getAbsolutePath());
                    continue;
                }

                // Проверяем размер файла
                if (file.length() == 0) {
                    log.warn("Файл {} пуст, пропускаем", file.getName());
                    continue;
                }

                log.debug("Добавляем в архив: {} -> {}", file.getName(), targetFileName);

                // Создаем запись в ZIP с желаемым именем
                ZipEntry zipEntry = new ZipEntry(targetFileName);
                zos.putNextEntry(zipEntry);

                // Копируем содержимое файла
                try (FileInputStream fis = new FileInputStream(file)) {
                    byte[] buffer = new byte[8192]; // 8KB буфер
                    int length;
                    while ((length = fis.read(buffer)) > 0) {
                        zos.write(buffer, 0, length);
                    }
                }

                zos.closeEntry();
                log.debug("Файл {} успешно добавлен в архив как {}",
                        file.getName(), targetFileName);
            }

            // Завершаем создание ZIP
            zos.finish();
            zos.flush();

        } catch (Exception e) {
            log.error("Ошибка при создании ZIP архива: {}", e.getMessage(), e);
            throw new IOException("Ошибка при создании ZIP архива: " + e.getMessage(), e);
        }

        byte[] result = baos.toByteArray();

        // Проверяем, что архив не пустой
        if (result.length == 0) {
            throw new IOException("Созданный ZIP архив пуст");
        }

        log.info("ZIP архив '{}' успешно создан, размер: {} байт, файлов: {}",
                zipName, result.length, files.size());

        return result;
    }
}
