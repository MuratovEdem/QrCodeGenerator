package controlm.qrcodegenerator.excel.service;

import controlm.qrcodegenerator.excel.config.MappingConfig;
import controlm.qrcodegenerator.excel.config.TemplateConfig;
import controlm.qrcodegenerator.excel.dto.FieldMapping;
import controlm.qrcodegenerator.excel.dto.TemplateMapping;
import controlm.qrcodegenerator.excel.dto.TemplateSubType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ExcelService {

    private final TemplateConfig templateConfig;

    /**
     * Генерирует временные файлы протоколов для каждой строки данных.
     */
    public Map<File, String> generateProtocols(MultipartFile requestFile, String templateType)
            throws IOException {

        // Получаем шаблон по типу
        TemplateMapping template = getTemplateByType(templateType);
        log.info("Используется шаблон: {} ({})", template.getName(), template.getType());

        Map<File, String> result = new HashMap<>();

        try (Workbook requestWorkbook = WorkbookFactory.create(requestFile.getInputStream())) {
            // Получаем данные из заявки
            List<Map<String, String>> data = parseRequestData(requestWorkbook);

            // Генерируем протоколы для каждой строки данных
            for (Map<String, String> rowData : data) {
                // Определяем конкретный шаблон для этой строки
                TemplateInfo templateInfo = resolveTemplateForRow(template, rowData);

                // Генерируем протокол
                File protocolFile = generateSingleProtocol(rowData, templateInfo);
                String fileName = getFileNameFromData(rowData);
                result.put(protocolFile, fileName);

                log.info("Сгенерирован протокол для класса бетона {}: {}",
                        rowData.get("Класс бетона"), fileName);
            }
        }

        return result;
    }

    /**
     * Определяет, какой конкретный шаблон использовать для строки данных
     */
    private TemplateInfo resolveTemplateForRow(TemplateMapping template, Map<String, String> rowData) {
        log.info("=== Выбор шаблона для строки данных ===");
        log.info("Тип шаблона: {}", template.getType());
        log.info("Данные строки: {}", rowData);

        if (!template.hasSubTypes()) {
            log.info("Шаблон без подтипов, используем основной: {}", template.getPath());
            log.info("Количество полей в основном шаблоне: {}", template.getFields().size());
            return new TemplateInfo(template.getPath(), template.getFields());
        }

        // Для 28 дней - определяем подтип по классу бетона
        String concreteClass = rowData.get("Класс бетона");
        log.info("Класс бетона из строки: '{}'", concreteClass);

        if (concreteClass == null || concreteClass.isEmpty()) {
            log.warn("Класс бетона не указан, используем первый подтип как запасной вариант");
            TemplateSubType defaultSubType = template.getSubTypes().get(0);
            log.info("Выбран подтип для класса {}: {}", defaultSubType.getConcreteClass(), defaultSubType.getPath());
            log.info("Количество полей в подтипе: {}", defaultSubType.getFields().size());
            return new TemplateInfo(defaultSubType.getPath(), defaultSubType.getFields());
        }

        // Ищем точное совпадение
        for (TemplateSubType subType : template.getSubTypes()) {
            log.info("Проверяем подтип для класса: {}", subType.getConcreteClass());
            if (subType.getConcreteClass().equals(concreteClass)) {
                log.info("Найдено точное совпадение! Используем подтип для класса {}", concreteClass);
                log.info("Путь к шаблону: {}", subType.getPath());
                log.info("Количество полей в подтипе: {}", subType.getFields().size());
                return new TemplateInfo(subType.getPath(), subType.getFields());
            }
        }

        // Проверяем группы
        if (concreteClass.equals("В25") || concreteClass.equals("В30")) {
            log.info("Класс {} входит в группу В25/В30", concreteClass);
            Optional<TemplateSubType> b25b30Template = template.getSubTypes().stream()
                    .filter(st -> st.getConcreteClass().equals("В25"))
                    .findFirst();

            if (b25b30Template.isPresent()) {
                log.info("Используем общий шаблон для В25/В30");
                log.info("Путь к шаблону: {}", b25b30Template.get().getPath());
                log.info("Количество полей в подтипе: {}", b25b30Template.get().getFields().size());
                return new TemplateInfo(b25b30Template.get().getPath(),
                        b25b30Template.get().getFields());
            }
        }

        // Если ничего не нашли, используем первый подтип
        log.warn("Не найден подтип для класса бетона {}, используем первый подтип", concreteClass);
        TemplateSubType defaultSubType = template.getSubTypes().get(0);
        log.info("Выбран подтип для класса {}: {}", defaultSubType.getConcreteClass(), defaultSubType.getPath());
        log.info("Количество полей в подтипе: {}", defaultSubType.getFields().size());
        return new TemplateInfo(defaultSubType.getPath(), defaultSubType.getFields());
    }

    /**
     * Генерирует один протокол
     */
    private File generateSingleProtocol(Map<String, String> data, TemplateInfo templateInfo)
            throws IOException {

        log.info("=== Генерация протокола ===");
        log.info("Путь к шаблону: {}", templateInfo.getPath());
        log.info("Количество полей для заполнения: {}", templateInfo.getFields().size());

        // Выводим все поля, которые будем заполнять
        for (FieldMapping field : templateInfo.getFields()) {
            log.info("Поле: {} -> ячейка {}.{}",
                    field.getSourceColumnName(),
                    field.getSheetName(),
                    field.getCellReference());
        }

        // Выводим все данные, которые есть
        log.info("Данные для заполнения:");
        for (Map.Entry<String, String> entry : data.entrySet()) {
            log.info("  {} = '{}'", entry.getKey(), entry.getValue());
        }

        File templateFile = new File(templateInfo.getPath());
        if (!templateFile.exists()) {
            log.error("Файл шаблона не найден: {}", templateInfo.getPath());
            throw new IOException("Файл шаблона не найден: " + templateInfo.getPath());
        }
        log.info("Файл шаблона существует, размер: {} байт", templateFile.length());

        // Создаем временный файл
        File tempFile = createTempFile();
        log.info("Создан временный файл: {}", tempFile.getAbsolutePath());

        // Копируем шаблон
        try (InputStream in = new FileInputStream(templateFile);
             OutputStream out = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            log.info("Шаблон скопирован во временный файл");
        }

        // Модифицируем файл
        try (FileInputStream fis = new FileInputStream(tempFile);
             Workbook workbook = new XSSFWorkbook(fis)) {

            int filledCount = 0;

            for (FieldMapping field : templateInfo.getFields()) {
                String value = data.get(field.getSourceColumnName());
                log.info("Обработка поля: {} -> значение: '{}'",
                        field.getSourceColumnName(), value);

                if (value == null || value.isEmpty()) {
                    log.warn("Значение для поля {} пустое, пропускаем", field.getSourceColumnName());
                    continue;
                }

                Sheet sheet = workbook.getSheet(field.getSheetName());
                if (sheet == null) {
                    log.warn("Лист '{}' не найден в шаблоне", field.getSheetName());
                    continue;
                }
                log.info("Найден лист: {}", field.getSheetName());

                CellReference cellRef = new CellReference(field.getCellReference());
                log.info("Ссылка на ячейку: row={}, col={}", cellRef.getRow(), cellRef.getCol());

                Row row = sheet.getRow(cellRef.getRow());
                if (row == null) {
                    log.info("Строка {} не существует, создаем новую", cellRef.getRow());
                    row = sheet.createRow(cellRef.getRow());
                }

                Cell cell = row.getCell(cellRef.getCol());
                if (cell == null) {
                    log.info("Ячейка {}.{} не существует, создаем новую",
                            cellRef.getRow(), cellRef.getCol());
                    cell = row.createCell(cellRef.getCol());
                }

                CellStyle originalStyle = cell.getCellStyle();
                log.info("Текущий тип ячейки: {}, стиль: {}",
                        cell.getCellType(), originalStyle != null ? "есть" : "нет");

                cell.setCellValue(value);
                if (originalStyle != null) {
                    cell.setCellStyle(originalStyle);
                    log.info("Стиль ячейки сохранен");
                }

                filledCount++;
                log.info("✓ Записано значение '{}' в ячейку {}", value, field.getCellReference());
            }

            log.info("Заполнено {} полей из {}", filledCount, templateInfo.getFields().size());

            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                workbook.write(fos);
                log.info("Изменения сохранены во временный файл");
            }
        }

        log.info("Протокол успешно сгенерирован: {}", tempFile.getAbsolutePath());
        return tempFile;
    }

    /**
     * Вспомогательный класс для передачи информации о шаблоне
     */
    private static class TemplateInfo {
        private final String path;
        private final List<FieldMapping> fields;

        public TemplateInfo(String path, List<FieldMapping> fields) {
            this.path = path;
            this.fields = fields;
        }

        public String getPath() {
            return path;
        }

        public List<FieldMapping> getFields() {
            return fields;
        }
    }

    /**
     * Получает шаблон по типу
     */
    private TemplateMapping getTemplateByType(String templateType) {
        return templateConfig.getTemplates().stream()
                .filter(t -> t.getType().equals(templateType))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Шаблон типа '" + templateType + "' не найден"));
    }

    /**
     * Парсит данные из заявки
     */
    private List<Map<String, String>> parseRequestData(Workbook workbook) {
        // Пытаемся найти Лист2
        Sheet sheet = workbook.getSheet("Лист1");

        if (sheet == null) {
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                String sheetName = workbook.getSheetName(i);
                if (sheetName.toLowerCase().contains("лист2") ||
                        sheetName.toLowerCase().contains("sheet2")) {
                    sheet = workbook.getSheetAt(i);
                    log.info("Найден похожий лист: {}", sheetName);
                    break;
                }
            }
        }

        if (sheet == null && workbook.getNumberOfSheets() > 1) {
            sheet = workbook.getSheetAt(1);
            log.info("Используем второй лист: {}", workbook.getSheetName(1));
        }

        if (sheet == null) {
            throw new IllegalArgumentException("Не найден лист с данными (ожидается Лист2)");
        }

        log.info("Обрабатываем лист: {}", sheet.getSheetName());

        Row headerRow = sheet.getRow(0);
        if (headerRow == null) {
            throw new IllegalArgumentException("Файл не содержит строки заголовков");
        }

        // Создаем маппинг заголовков
        Map<String, Integer> headerMap = new HashMap<>();
        for (Cell cell : headerRow) {
            String headerValue = getCellValueAsString(cell).trim();
            if (!headerValue.isEmpty()) {
                headerMap.put(headerValue, cell.getColumnIndex());
                log.info("Найден заголовок: '{}' в колонке {}", headerValue, cell.getColumnIndex());
            }
        }

        List<Map<String, String>> data = new ArrayList<>();

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null || isRowEmpty(row)) continue;

            Map<String, String> rowData = new HashMap<>();

            // Проходим по всем ячейкам строки
            for (Cell cell : row) {
                int colIndex = cell.getColumnIndex();
                String value = getCellValueAsString(cell).trim();

                // Находим соответствующий заголовок
                for (Map.Entry<String, Integer> entry : headerMap.entrySet()) {
                    if (entry.getValue() == colIndex) {
                        rowData.put(entry.getKey(), value);
                        break;
                    }
                }
            }

            if (!rowData.isEmpty()) {
                data.add(rowData);
                log.info("Найдена строка данных {}: {}", i, rowData);
            }
        }

        if (data.isEmpty()) {
            throw new IllegalArgumentException("Файл не содержит данных после заголовка");
        }

        return data;
    }

    /**
     * Создает временный файл
     */
    private File createTempFile() throws IOException {
        String tempDir = System.getProperty("java.io.tmpdir");
        return File.createTempFile("protocol_", ".xlsx", new File(tempDir));
    }

    /**
     * Формирует имя файла из данных
     */
    private String getFileNameFromData(Map<String, String> data) {
        String protocolNumber = data.get("Номер протокола");
        if (protocolNumber == null || protocolNumber.isEmpty()) {
            protocolNumber = "protocol_" + UUID.randomUUID().toString().substring(0, 8);
        }
        return sanitizeFileName(protocolNumber) + ".xlsx";
    }

    /**
     * Очищает имя файла от недопустимых символов
     */
    private String sanitizeFileName(String fileName) {
        return fileName
                .replaceAll("[\\\\/:*?\"<>|]", "_")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private boolean isRowEmpty(Row row) {
        if (row == null) return true;
        for (int i = row.getFirstCellNum(); i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK &&
                    !getCellValueAsString(cell).trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    private String getCellValueAsString(Cell cell) {
        if (cell == null) return "";

        try {
            switch (cell.getCellType()) {
                case STRING:
                    return cell.getStringCellValue();
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        return cell.getDateCellValue().toString();
                    } else {
                        double numericValue = cell.getNumericCellValue();
                        if (numericValue == Math.floor(numericValue)) {
                            return String.valueOf((long) numericValue);
                        }
                        return String.valueOf(numericValue);
                    }
                case BOOLEAN:
                    return String.valueOf(cell.getBooleanCellValue());
                case FORMULA:
                    try {
                        return cell.getStringCellValue();
                    } catch (IllegalStateException e) {
                        return String.valueOf(cell.getNumericCellValue());
                    }
                default:
                    return "";
            }
        } catch (Exception e) {
            log.warn("Ошибка при чтении ячейки: {}", e.getMessage());
            return "";
        }
    }
}
