package controlm.qrcodegenerator.excel.service;

import controlm.qrcodegenerator.excel.config.MappingConfig;
import controlm.qrcodegenerator.excel.dto.FieldMapping;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellReference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExcelService {
    @Value("${protocol.template.path}")
    private String templatePath;

    private final MappingConfig mappingConfig;


    /**
     * Парсит файл заявки, возвращает список строк данных.
     * Каждая строка представлена Map, где ключ – имя столбца (sourceColumnName), значение – ячейка.
     */
    public List<Map<String, String>> parseRequestFile(MultipartFile file) throws IOException {
        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0); // работаем с первым листом
            Row headerRow = sheet.getRow(0);
            if (headerRow == null) {
                throw new IllegalArgumentException("Файл заявки не содержит строки заголовков");
            }

            // Строим соответствие "имя столбца" -> "индекс колонки"
            Map<String, Integer> headerMap = new HashMap<>();
            for (Cell cell : headerRow) {
                String headerValue = cell.getStringCellValue();
                headerMap.put(headerValue, cell.getColumnIndex());
            }

            List<Map<String, String>> rows = new ArrayList<>();
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;

                Map<String, String> rowData = new HashMap<>();
                for (FieldMapping field : mappingConfig.getFields()) {
                    String columnName = field.getSourceColumnName();
                    Integer colIndex = headerMap.get(columnName);
                    if (colIndex != null) {
                        Cell cell = row.getCell(colIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                        String value = getCellValueAsString(cell);
                        rowData.put(columnName, value);
                    }
                }
                if (!rowData.isEmpty()) {
                    rows.add(rowData);
                }
            }
            return rows;
        }
    }

    /**
     * Генерирует временные файлы протоколов для каждой строки данных.
     */
    public List<File> generateProtocols(List<Map<String, String>> data) throws IOException {
        File templateFile = new File(templatePath);
        if (!templateFile.exists()) {
            throw new IOException("Файл шаблона не найден: " + templatePath);
        }

        List<File> generatedFiles = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            Map<String, String> rowData = data.get(i);
            // Создаём временный файл как копию шаблона
            File tempFile = File.createTempFile("protocol_" + i, ".xlsx");
            Files.copy(templateFile.toPath(), tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

            try (Workbook workbook = WorkbookFactory.create(tempFile)) {
                for (FieldMapping field : mappingConfig.getFields()) {
                    String value = rowData.get(field.getSourceColumnName());
                    if (value == null || value.isEmpty()) continue;

                    Sheet sheet = workbook.getSheet(field.getSheetName());
                    if (sheet == null) continue;

                    CellReference cellRef = new CellReference(field.getCellReference());
                    Row row = sheet.getRow(cellRef.getRow());
                    if (row == null) row = sheet.createRow(cellRef.getRow());
                    Cell cell = row.getCell(cellRef.getCol());
                    if (cell == null) cell = row.createCell(cellRef.getCol());

                    // Записываем значение как строку (формат ячейки сохранится)
                    cell.setCellValue(value);
                }
                // Сохраняем изменения
                try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                    workbook.write(fos);
                }
            }
            generatedFiles.add(tempFile);
        }
        return generatedFiles;
    }

    /**
     * Извлекает строковое значение из ячейки Excel.
     */
    private String getCellValueAsString(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString(); // можно форматировать через SimpleDateFormat
                } else {
                    // Убираем лишние нули у целых чисел
                    double numericValue = cell.getNumericCellValue();
                    if (numericValue == Math.floor(numericValue)) {
                        return String.valueOf((long) numericValue);
                    }
                    return String.valueOf(numericValue);
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula(); // или вычислить значение через evaluator
            default:
                return "";
        }
    }
}
