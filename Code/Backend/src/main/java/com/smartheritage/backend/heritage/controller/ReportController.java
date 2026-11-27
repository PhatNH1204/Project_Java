package com.smartheritage.backend.heritage.controller;

import com.smartheritage.backend.common.ApiResponse;
import com.smartheritage.backend.heritage.dto.AnalyticsDashboard;
import com.smartheritage.backend.heritage.service.ReportService;
import java.time.LocalDate;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
@PreAuthorize("hasRole('ADMIN')")
public class ReportController {

    private final ReportService reports;

    public ReportController(ReportService reports) {
        this.reports = reports;
    }

    @GetMapping("/daily")
    public ApiResponse<AnalyticsDashboard> daily() {
        return ApiResponse.of(reports.dashboard("daily"));
    }

    @GetMapping("/weekly")
    public ApiResponse<AnalyticsDashboard> weekly() {
        return ApiResponse.of(reports.dashboard("weekly"));
    }

    @GetMapping("/monthly")
    public ApiResponse<AnalyticsDashboard> monthly() {
        return ApiResponse.of(reports.dashboard("monthly"));
    }

    @GetMapping("/export/pdf")
    public ResponseEntity<byte[]> pdf(
            @RequestParam(defaultValue = "monthly") String period) {
        String filename = "smart-heritage-" + period + "-" + LocalDate.now() + ".pdf";
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(filename).build().toString())
                .body(reports.pdf(period));
    }

    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> excel(
            @RequestParam(defaultValue = "monthly") String period) {
        String filename = "smart-heritage-" + period + "-" + LocalDate.now() + ".xlsx";
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(
                        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(filename).build().toString())
                .body(reports.excel(period));
    }
}
