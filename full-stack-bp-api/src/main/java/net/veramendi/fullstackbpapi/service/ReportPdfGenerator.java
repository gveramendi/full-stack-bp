package net.veramendi.fullstackbpapi.service;

import net.veramendi.fullstackbpapi.web.dto.report.ReportResponse;
import net.veramendi.fullstackbpapi.web.dto.report.ReportResponse.AccountStatement;
import net.veramendi.fullstackbpapi.web.dto.report.ReportResponse.MovementEntry;

import org.openpdf.text.Document;
import org.openpdf.text.DocumentException;
import org.openpdf.text.Element;
import org.openpdf.text.Font;
import org.openpdf.text.FontFactory;
import org.openpdf.text.PageSize;
import org.openpdf.text.Paragraph;
import org.openpdf.text.Phrase;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;

import org.springframework.stereotype.Component;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

@Component
public class ReportPdfGenerator {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final Font H1 = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, Color.BLACK);
    private static final Font H2 = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Color.BLACK);
    private static final Font NORMAL = FontFactory.getFont(FontFactory.HELVETICA, 10, Color.BLACK);
    private static final Font TABLE_HEAD = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Color.WHITE);
    private static final Color HEADER_BG = new Color(0x33, 0x66, 0x99);

    public byte[] render(ReportResponse report) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document doc = new Document(PageSize.A4, 36, 36, 48, 36);
            PdfWriter.getInstance(doc, baos);
            doc.open();

            doc.add(new Paragraph("Account Statement", H1));
            doc.add(new Paragraph("Client: %s (%s)".formatted(report.clientName(), report.clientId()), NORMAL));
            doc.add(new Paragraph("Period: %s to %s".formatted(report.from(), report.to()), NORMAL));
            doc.add(new Paragraph(" "));

            for (AccountStatement account : report.accounts()) {
                addAccountSection(doc, account);
            }

            doc.add(new Paragraph(" "));
            doc.add(new Paragraph(
                    "Total credits: %s   Total debits: %s".formatted(report.totalCredits(), report.totalDebits()),
                    H2));

            doc.close();

            return baos.toByteArray();
        } catch (DocumentException | IOException ex) {
            throw new IllegalStateException("Failed to generate PDF report", ex);
        }
    }

    private void addAccountSection(Document doc, AccountStatement account) throws DocumentException {
        doc.add(new Paragraph(
                "%s — %s".formatted(account.accountNumber(), account.accountType()), H2));
        doc.add(new Paragraph(
                "Initial: %s   Current: %s   Credits: %s   Debits: %s".formatted(
                        account.initialBalance(), account.currentBalance(),
                        account.totalCredits(), account.totalDebits()),
                NORMAL));

        PdfPTable table = new PdfPTable(new float[]{3, 2, 2, 2});
        table.setWidthPercentage(100);
        table.setSpacingBefore(6);
        table.setSpacingAfter(12);

        addHeader(table, "Date");
        addHeader(table, "Type");
        addHeader(table, "Value");
        addHeader(table, "Balance");

        if (account.movements().isEmpty()) {
            PdfPCell empty = new PdfPCell(new Phrase("No movements in this period", NORMAL));
            empty.setColspan(4);
            empty.setHorizontalAlignment(Element.ALIGN_CENTER);
            empty.setPadding(8);
            table.addCell(empty);
        } else {
            for (MovementEntry m : account.movements()) {
                addCell(table, m.date().format(DATE_FMT));
                addCell(table, m.movementType().name());
                addCell(table, m.value().toPlainString());
                addCell(table, m.balance().toPlainString());
            }
        }
        
        doc.add(table);
    }

    private void addHeader(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, TABLE_HEAD));
        cell.setBackgroundColor(HEADER_BG);
        cell.setPadding(6);
        table.addCell(cell);
    }

    private void addCell(PdfPTable table, String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, NORMAL));
        cell.setPadding(4);
        table.addCell(cell);
    }
}
