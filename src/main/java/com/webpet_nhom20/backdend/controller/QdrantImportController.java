package com.webpet_nhom20.backdend.controller;



import com.webpet_nhom20.backdend.dto.chatbot.ExcelImportResult;
import com.webpet_nhom20.backdend.service.Chatbot.ExcelImportService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/qdrant")
public class QdrantImportController {
    private final ExcelImportService excelImportService;

    public QdrantImportController(ExcelImportService excelImportService) {
        this.excelImportService = excelImportService;
    }

    @PostMapping(value = "/import-excel", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> importExcel(
            @RequestParam("file") MultipartFile file,
            @RequestParam(defaultValue = "products") String sheetName
    ) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("File is empty");
        }

        ExcelImportResult result =
                excelImportService.importToQdrant(file, sheetName);

        return ResponseEntity.ok(result);
    }
}
