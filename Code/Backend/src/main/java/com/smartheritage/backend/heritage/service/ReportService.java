package com.smartheritage.backend.heritage.service;

import com.smartheritage.backend.heritage.dto.AnalyticsDashboard;
import com.smartheritage.backend.heritage.dto.ArtifactPopularity;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.Standard14Fonts;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

@Service
public class ReportService {

    private final AnalyticsService analytics;

    public ReportService(AnalyticsService analytics) {
        this.analytics = analytics;
    }

    public AnalyticsDashboard dashboard(String period) {
        return analytics.dashboard(period);
    }

    public byte[] excel(String period) {
        AnalyticsDashboard report = analytics.dashboard(period);
        try (XSSFWorkbook workbook = new XSSFWorkbook();
                ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            Sheet summary = workbook.createSheet("Summary");
            String[][] rows = {
                {"Metric", "Value"},
                {"Period", report.period()},
                {"From (UTC)", report.from().toString()},
                {"Total visitors", Long.toString(report.totalVisitors())},
                {"Visitors in period", Long.toString(report.visitorsInPeriod())},
                {"Interactions in period", Long.toString(report.interactionsInPeriod())},
                {"Average engagement seconds", Long.toString(report.averageEngagementSeconds())},
                {"Average approved rating", Double.toString(report.averageRating())},
                {"Pending feedback", Long.toString(report.pendingFeedback())}
            };
            for (int i = 0; i < rows.length; i++) {
                Row row = summary.createRow(i);
                row.createCell(0).setCellValue(rows[i][0]);
                row.createCell(1).setCellValue(rows[i][1]);
            }
            summary.autoSizeColumn(0);
            summary.autoSizeColumn(1);

            Sheet popular = workbook.createSheet("Popular artifacts");
            Row header = popular.createRow(0);
            header.createCell(0).setCellValue("Artifact ID");
            header.createCell(1).setCellValue("Artifact code");
            header.createCell(2).setCellValue("Interactions");
            writePopularRows(popular, report.popularArtifacts());
            popular.autoSizeColumn(0);
            popular.autoSizeColumn(1);
            popular.autoSizeColumn(2);

            Sheet languages = workbook.createSheet("Languages");
            Row languageHeader = languages.createRow(0);
            languageHeader.createCell(0).setCellValue("Language");
            languageHeader.createCell(1).setCellValue("Interactions");
            int rowIndex = 1;
            for (var entry : report.languageDistribution().entrySet()) {
                Row row = languages.createRow(rowIndex++);
                row.createCell(0).setCellValue(entry.getKey());
                row.createCell(1).setCellValue(entry.getValue());
            }
            languages.autoSizeColumn(0);
            languages.autoSizeColumn(1);
            workbook.write(output);
            return output.toByteArray();
        } catch (IOException exception) {
            throw new ReportGenerationException("Unable to generate Excel report", exception);
        }
    }

    public byte[] pdf(String period) {
        AnalyticsDashboard report = analytics.dashboard(period);
        try (PDDocument document = new PDDocument();
                ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            PDPage page = new PDPage(PDRectangle.LETTER);
            document.addPage(page);
            try (PDPageContentStream stream = new PDPageContentStream(document, page)) {
                stream.beginText();
                stream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA_BOLD), 16);
                stream.newLineAtOffset(50, 740);
                stream.showText("Smart Heritage - " + report.period() + " report");
                stream.newLineAtOffset(0, -28);
                stream.setFont(new PDType1Font(Standard14Fonts.FontName.HELVETICA), 11);
                for (String line : reportLines(report)) {
                    stream.showText(line);
                    stream.newLineAtOffset(0, -18);
                }
                stream.endText();
            }
            document.save(output);
            return output.toByteArray();
        } catch (IOException exception) {
            throw new ReportGenerationException("Unable to generate PDF report", exception);
        }
    }

    private List<String> reportLines(AnalyticsDashboard report) {
        List<String> lines = new ArrayList<>();
        lines.add("Period starts: " + report.from());
        lines.add("Total visitors: " + report.totalVisitors());
        lines.add("Visitors in period: " + report.visitorsInPeriod());
        lines.add("Interactions: " + report.interactionsInPeriod());
        lines.add("Average engagement (seconds): " + report.averageEngagementSeconds());
        lines.add("Average approved rating: " + report.averageRating());
        lines.add("Pending feedback: " + report.pendingFeedback());
        lines.add("Top artifacts:");
        for (ArtifactPopularity artifact : report.popularArtifacts().stream().limit(12).toList()) {
            lines.add(artifact.artifactCode() + " - " + artifact.interactions() + " interactions");
        }
        return lines;
    }

    private void writePopularRows(Sheet sheet, List<ArtifactPopularity> artifacts) {
        int rowIndex = 1;
        for (ArtifactPopularity artifact : artifacts) {
            Row row = sheet.createRow(rowIndex++);
            row.createCell(0).setCellValue(artifact.artifactId());
            row.createCell(1).setCellValue(artifact.artifactCode());
            row.createCell(2).setCellValue(artifact.interactions());
        }
    }
}
