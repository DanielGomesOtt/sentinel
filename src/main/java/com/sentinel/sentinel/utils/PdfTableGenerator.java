package com.sentinel.sentinel.utils;

import org.openpdf.text.*;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.RecordComponent;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PdfTableGenerator {

    public <T> byte[] generatePdf(List<T> data, String title) throws Exception {

        if (data == null || data.isEmpty()) {
            throw new IllegalArgumentException("The list is empty.");
        }

        T firstItem = data.getFirst();

        if (!firstItem.getClass().isRecord()) {
            throw new IllegalArgumentException("The DTO must be a Record.");
        }

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        Document document = new Document(PageSize.A4.rotate());

        PdfWriter.getInstance(document, outputStream);

        document.open();


        Font titleFont = FontFactory.getFont(
                FontFactory.HELVETICA_BOLD,
                18
        );

        Paragraph titleParagraph = new Paragraph(title, titleFont);
        titleParagraph.setAlignment(Element.ALIGN_CENTER);
        titleParagraph.setSpacingAfter(20);

        document.add(titleParagraph);

        Paragraph generatedAt = new Paragraph(
                "Generated at: " +
                        LocalDateTime.now().format(
                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                        )
        );

        generatedAt.setAlignment(Element.ALIGN_CENTER);
        generatedAt.setSpacingAfter(15);

        document.add(generatedAt);

        RecordComponent[] columns =
                firstItem.getClass().getRecordComponents();

        PdfPTable table = new PdfPTable(columns.length);
        table.setWidthPercentage(100);


        Font headerFont = FontFactory.getFont(
                FontFactory.HELVETICA_BOLD
        );

        for (RecordComponent column : columns) {

            PdfPCell cell = new PdfPCell(
                    new Phrase(column.getName(), headerFont)
            );

            cell.setHorizontalAlignment(Element.ALIGN_CENTER);

            table.addCell(cell);
        }


        for (T row : data) {

            for (RecordComponent column : columns) {

                Object value = column.getAccessor().invoke(row);

                table.addCell(
                        value != null
                                ? value.toString()
                                : ""
                );
            }
        }

        document.add(table);

        document.close();

        return outputStream.toByteArray();
    }
}
