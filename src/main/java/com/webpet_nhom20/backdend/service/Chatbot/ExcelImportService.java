package com.webpet_nhom20.backdend.service.Chatbot;



import com.fasterxml.jackson.databind.ObjectMapper;
import com.webpet_nhom20.backdend.dto.chatbot.ExcelImportResult;
import com.webpet_nhom20.backdend.dto.chatbot.ImportRow;
import com.webpet_nhom20.backdend.dto.chatbot.QdrantPoint;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.UUID;

@Service
public class ExcelImportService {
    private static final int BATCH_SIZE = 64;
    private final QdrantService qdrantService;
    private final EmbeddingService embeddingService;

    public ExcelImportService(QdrantService qdrantService, EmbeddingService embeddingService) {
        this.qdrantService = qdrantService;
        this.embeddingService = embeddingService;
    }

    public ExcelImportResult importToQdrant(
            MultipartFile file,
            String sheetName
    ) {
        int success = 0;
        int skipped = 0;
        List<String> errors = new ArrayList<>();

        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {

            Sheet sheet;
            if (sheetName == null || sheetName.isBlank()) {
                sheet = workbook.getSheetAt(0); // 👈 FIX
            } else {
                sheet = workbook.getSheet(sheetName);
            }

            if (sheet == null) {
                throw new IllegalArgumentException("Sheet not found: " + sheetName);
            }


            Iterator<Row> rows = sheet.iterator();
            if (!rows.hasNext()) {
                return new ExcelImportResult(0, 0, List.of("Empty sheet"));
            }

            Map<String, Integer> headerIndex = buildHeaderIndex(rows.next());

            List<QdrantPoint> batch = new ArrayList<>();

            while (rows.hasNext()) {
                Row row = rows.next();

                try {
                    ImportRow rowData = parseRow(row, headerIndex);

                    if (!rowData.isValid()) {
                        skipped++;
                        continue;
                    }

                    Map<String, Object> payload = buildPayload(rowData);
                    String content = (String) payload.get("content");
                    float[] vector = embeddingService.embed(content);

                    batch.add(new QdrantPoint(
                            normalizeId(rowData.getId()),
                            vector,
                            payload
                    ));
                    System.out.println("Current batch size = " + batch.size());

                    if (batch.size() >= BATCH_SIZE) {
                        ObjectMapper mapper = new ObjectMapper();
                        System.out.println(
                                mapper.writerWithDefaultPrettyPrinter()
                                        .writeValueAsString(batch)
                        );
                        qdrantService.upsertPoints(batch);
                        success += batch.size();
                        batch.clear();
                    }

                } catch (Exception e) {
                    skipped++;
                    errors.add("Row " + row.getRowNum() + ": " + e.getMessage());
                }
            }

            if (!batch.isEmpty()) {
                ObjectMapper mapper = new ObjectMapper();
                System.out.println("===== LAST BATCH =====");
                System.out.println(
                        mapper.writerWithDefaultPrettyPrinter()
                                .writeValueAsString(batch)
                );
                qdrantService.upsertPoints(batch);
                success += batch.size();
            }

        } catch (Exception e) {
            errors.add("Import failed: " + e.getMessage());
        }

        return new ExcelImportResult(success, skipped, errors);
    }
    private ImportRow parseRow(Row r, Map<String, Integer> h) {
        ImportRow row = new ImportRow();

        row.setId(getString(r, h.get("id")));
        row.setType(getString(r, h.get("type")));
        row.setStatus(getString(r, h.get("status")));
        row.setName(getString(r, h.get("name")));

        row.setAnimal(getString(r, h.get("animal")));
        row.setCategory(getString(r, h.get("category")));
        row.setBrand(getString(r, h.get("brand")));
        row.setKeyFeatures(getString(r, h.get("key_features")));
        row.setWarnings(getString(r, h.get("warnings")));
        row.setSource(getString(r, h.get("source")));
        row.setPrice(getNumber(r, h.get("price")));

        return row;
    }
    private Map<String, Object> buildPayload(ImportRow r) {
        Map<String, Object> p = new HashMap<>();

        // REQUIRED – chắc chắn có
        p.put("id", r.getId());
        p.put("type", r.getType());
        p.put("status", r.getStatus());
        p.put("name", r.getName());

        // OPTIONAL – chỉ put khi hợp lệ
        if (notBlank(r.getAnimal())) {
            p.put("animal", r.getAnimal());
        }

        if (notBlank(r.getCategory())) {
            p.put("category", r.getCategory());
        }

        if (notBlank(r.getBrand())) {
            p.put("brand", r.getBrand());
        }

        if (r.getPrice() != null) {
            p.put("price", r.getPrice());
        }

        if (notBlank(r.getKeyFeatures())) {
            p.put("key_features", r.getKeyFeatures());
        }

        if (notBlank(r.getWarnings())) {
            p.put("warnings", r.getWarnings());
        }

        if (notBlank(r.getSource())) {
            p.put("source", r.getSource());
        }

        // content dùng cho embedding – chỉ put nếu không rỗng
        String content = buildEmbeddingText(r);
        if (notBlank(content)) {
            p.put("content", content);
        }

        return p;
    }

    private boolean notBlank(String s) {
        return s != null && !s.trim().isEmpty();
    }


    private String buildEmbeddingText(ImportRow r) {
        StringBuilder sb = new StringBuilder();

        sb.append("name: ").append(r.getName()).append("\n");

        if (r.getPrice() != null) {
            sb.append("price: ").append(r.getPrice()).append("\n");
        }
        if (notBlank(r.getBrand())) {
            sb.append("brand: ").append(r.getBrand()).append("\n");
        }
        if (notBlank(r.getCategory())) {
            sb.append("category: ").append(r.getCategory()).append("\n");
        }
        if (notBlank(r.getAnimal())) {
            sb.append("animal: ").append(r.getAnimal()).append("\n");
        }
        if (notBlank(r.getKeyFeatures())) {
            sb.append("features: ").append(r.getKeyFeatures()).append("\n");
        }
        if (notBlank(r.getWarnings())) {
            sb.append("warnings: ").append(r.getWarnings()).append("\n");
        }

        sb.append("status: ").append(r.getStatus());
        return sb.toString();
    }

    private Object normalizeId(String rawId) {
        if (rawId == null || rawId.trim().isEmpty()) {
            return UUID.randomUUID().toString();
        }

        String id = rawId.trim();
        if (id.matches("^[0-9]+$")) {
            try {
                return Long.parseLong(id);
            } catch (NumberFormatException ignored) {
                // fall through to UUID
            }
        }

        if (id.matches("^[0-9a-fA-F-]{36}$")) {
            return id;
        }

        return UUID.nameUUIDFromBytes(id.getBytes(StandardCharsets.UTF_8)).toString();
    }
    private String getString(Row row, Integer colIndex) {
        if (colIndex == null || colIndex < 0) {
            return "";
        }

        Cell cell = row.getCell(colIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) {
            return "";
        }

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> {
                // Nếu là số nhưng user nhập kiểu text (ví dụ id, price)
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield cell.getDateCellValue().toString();
                }
                yield String.valueOf(cell.getNumericCellValue());
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            case FORMULA -> {
                // Lấy kết quả đã tính của formula
                try {
                    yield cell.getStringCellValue();
                } catch (IllegalStateException e) {
                    yield String.valueOf(cell.getNumericCellValue());
                }
            }
            default -> "";
        };
    }
    private Double getNumber(Row row, Integer colIndex) {
        if (colIndex == null || colIndex < 0) {
            return null;
        }

        Cell cell = row.getCell(colIndex, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
        if (cell == null) {
            return null;
        }

        return switch (cell.getCellType()) {
            case NUMERIC -> cell.getNumericCellValue();
            case STRING -> {
                String s = cell.getStringCellValue().trim();
                if (s.isEmpty()) yield null;
                try {
                    yield Double.parseDouble(s);
                } catch (NumberFormatException e) {
                    yield null;
                }
            }
            default -> null;
        };
    }
    private Map<String, Integer> buildHeaderIndex(Row headerRow) {
        Map<String, Integer> indexMap = new HashMap<>();

        for (Cell cell : headerRow) {
            if (cell.getCellType() != CellType.STRING) {
                continue;
            }

            String headerName = cell.getStringCellValue()
                    .trim()
                    .toLowerCase();

            if (!headerName.isEmpty()) {
                indexMap.put(headerName, cell.getColumnIndex());
            }
        }

        // Validate các cột bắt buộc
        List<String> requiredColumns = List.of(
                "id",
                "type",
                "status",
                "name"
        );

        for (String col : requiredColumns) {
            if (!indexMap.containsKey(col)) {
                throw new IllegalArgumentException(
                        "Missing required column in Excel: " + col
                );
            }
        }

        return indexMap;
    }





}
