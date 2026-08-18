package com.sentinel.sentinel.utils;

import com.sentinel.sentinel.dto.incident_log.IncidentLogPdfDTO;
import org.openpdf.text.*;
import org.openpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class IncidentLogPdfGenerator {

    public byte[] generatePdf(
            List<IncidentLogPdfDTO> logs,
            String title
    ) throws Exception {

        if (logs == null || logs.isEmpty()) {
            throw new IllegalArgumentException("The list is empty.");
        }

        ByteArrayOutputStream outputStream =
                new ByteArrayOutputStream();

        Document document =
                new Document(
                        PageSize.A4,
                        36,
                        36,
                        36,
                        36
                );

        PdfWriter.getInstance(document, outputStream);

        document.open();

        addTitle(document, title);

        addSummary(document, logs);

        for (int i = 0; i < logs.size(); i++) {

            addLogSection(
                    document,
                    i + 1,
                    logs.get(i)
            );

            if (i < logs.size() - 1) {
                document.add(new Paragraph(" "));
            }
        }

        document.close();

        return outputStream.toByteArray();
    }

    private void addTitle(
            Document document,
            String title
    ) throws Exception {

        Font titleFont =
                FontFactory.getFont(
                        FontFactory.HELVETICA_BOLD,
                        18
                );

        Paragraph titleParagraph =
                new Paragraph(title, titleFont);

        titleParagraph.setAlignment(
                Element.ALIGN_CENTER
        );

        titleParagraph.setSpacingAfter(15);

        document.add(titleParagraph);

        Paragraph generatedAt =
                new Paragraph(
                        "Generated at: "
                                + LocalDateTime.now().format(
                                DateTimeFormatter.ofPattern(
                                        "yyyy-MM-dd HH:mm:ss"
                                )
                        )
                );

        generatedAt.setAlignment(
                Element.ALIGN_CENTER
        );

        generatedAt.setSpacingAfter(20);

        document.add(generatedAt);
    }

    private void addSummary(
            Document document,
            List<IncidentLogPdfDTO> logs
    ) throws Exception {

        long infoCount =
                logs.stream()
                        .filter(log ->
                                "INFO".equalsIgnoreCase(
                                        String.valueOf(log.level())
                                ))
                        .count();

        long warnCount =
                logs.stream()
                        .filter(log ->
                                "WARN".equalsIgnoreCase(
                                        String.valueOf(log.level())
                                ))
                        .count();

        long errorCount =
                logs.stream()
                        .filter(log ->
                                "ERROR".equalsIgnoreCase(
                                        String.valueOf(log.level())
                                ))
                        .count();

        Font headerFont =
                FontFactory.getFont(
                        FontFactory.HELVETICA_BOLD,
                        14
                );

        document.add(
                new Paragraph(
                        "Summary",
                        headerFont
                )
        );

        document.add(
                new Paragraph(
                        "Total Logs: " + logs.size()
                )
        );

        document.add(
                new Paragraph(
                        "INFO: " + infoCount
                )
        );

        document.add(
                new Paragraph(
                        "WARN: " + warnCount
                )
        );

        document.add(
                new Paragraph(
                        "ERROR: " + errorCount
                )
        );

        document.add(new Paragraph(" "));
    }

    private void addLogSection(
            Document document,
            int index,
            IncidentLogPdfDTO log
    ) throws Exception {

        Font sectionFont =
                FontFactory.getFont(
                        FontFactory.HELVETICA_BOLD,
                        13
                );

        Font labelFont =
                FontFactory.getFont(
                        FontFactory.HELVETICA_BOLD
                );

        Font stackTraceFont =
                FontFactory.getFont(
                        FontFactory.COURIER,
                        8
                );

        Paragraph sectionTitle =
                new Paragraph(
                        "LOG #" + index,
                        sectionFont
                );

        sectionTitle.setSpacingBefore(10);
        sectionTitle.setSpacingAfter(10);

        document.add(sectionTitle);

        addField(
                document,
                "Date",
                DateTimeFormatter
                        .ofPattern("yyyy-MM-dd HH:mm:ss")
                        .withZone(ZoneId.systemDefault())
                        .format(log.createdAt()),
                labelFont
        );

        addField(
                document,
                "Level",
                String.valueOf(log.level()),
                labelFont
        );

        addField(
                document,
                "Service",
                String.valueOf(log.service_name()),
                labelFont
        );

        addField(
                document,
                "Message",
                String.valueOf(log.message()),
                labelFont
        );

        if (log.stack_trace() != null
                && !log.stack_trace().isBlank()) {

            document.add(
                    new Paragraph(
                            "Stack Trace:",
                            labelFont
                    )
            );

            document.add(
                    new Paragraph(
                            truncateStackTrace(
                                    log.stack_trace()
                            ),
                            stackTraceFont
                    )
            );
        }

        document.add(
                new Paragraph(
                        "-------------------------------------------------------"
                )
        );
    }

    private void addField(
            Document document,
            String label,
            String value,
            Font labelFont
    ) throws Exception {

        Paragraph paragraph =
                new Paragraph();

        paragraph.add(
                new Chunk(
                        label + ": ",
                        labelFont
                )
        );

        paragraph.add(
                new Chunk(
                        value != null
                                ? value
                                : ""
                )
        );

        document.add(paragraph);
    }

    private String truncateStackTrace(
            String stackTrace
    ) {

        String[] lines =
                stackTrace.split("\n");

        if (lines.length <= 30) {
            return stackTrace;
        }

        StringBuilder builder =
                new StringBuilder();

        for (int i = 0; i < 30; i++) {
            builder.append(lines[i])
                    .append("\n");
        }

        builder.append(
                "\n... Stack trace truncated ..."
        );

        return builder.toString();
    }
}
