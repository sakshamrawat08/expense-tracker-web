package com.expensetracker.controller;

import com.expensetracker.model.Expense;
import com.expensetracker.model.User;
import com.expensetracker.service.ExpenseService;
import com.expensetracker.service.UserService;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletResponse;

import java.io.*;
import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/export")
public class ExportController {

    private final ExpenseService expenseService;
    private final UserService userService;

    public ExportController(ExpenseService expenseService, UserService userService) {
        this.expenseService = expenseService;
        this.userService = userService;
    }

    // ================= PDF EXPORT =================
    @GetMapping("/pdf")
    public void exportPdf(Principal principal, HttpServletResponse response) throws IOException {

        User user = userService.findByUsername(principal.getName());
        List<Expense> expenses = expenseService.getAllExpenses(user);
        Double total = expenseService.getTotal(user);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=expenses.pdf");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document doc = new Document(pdf);

        // Title
        doc.add(new Paragraph("Expense Report - " + user.getUsername())
                .setFontSize(20)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER));

        // Total
        doc.add(new Paragraph("Total Spent: Rs. " + String.format("%.2f", total))
                .setFontSize(14)
                .setTextAlignment(TextAlignment.CENTER)
                .setFontColor(ColorConstants.BLUE));

        doc.add(new Paragraph(" "));

        // Table
        Table table = new Table(UnitValue.createPercentArray(new float[]{2, 2, 3, 2, 2}));
        table.setWidth(UnitValue.createPercentValue(100));

        // Headers
        String[] headers = {"Date", "Category", "Description", "Amount", "Bill"};
        for (String h : headers) {
            table.addHeaderCell(
                    new com.itextpdf.layout.element.Cell()
                            .add(new Paragraph(h).setBold())
                            .setBackgroundColor(ColorConstants.LIGHT_GRAY)
            );
        }

        // Data
        for (Expense e : expenses) {
            table.addCell(e.getDate().toString());
            table.addCell(e.getCategory());
            table.addCell(e.getDescription() != null ? e.getDescription() : "");
            table.addCell("Rs. " + String.format("%.2f", e.getAmount()));
            table.addCell(e.getBillImageUrl() != null ? "Yes" : "No");
        }

        doc.add(table);
        doc.close();

        response.getOutputStream().write(baos.toByteArray());
    }

    // ================= EXCEL EXPORT =================
    @GetMapping("/excel")
    public void exportExcel(Principal principal, HttpServletResponse response) throws IOException {

        User user = userService.findByUsername(principal.getName());
        List<Expense> expenses = expenseService.getAllExpenses(user);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=expenses.xlsx");

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Expenses");

        // Header style
        CellStyle headerStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        headerStyle.setFont(font);
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        // Header row
        Row header = sheet.createRow(0);
        String[] cols = {"ID", "Date", "Category", "Description", "Amount (Rs.)", "Has Bill"};

        for (int i = 0; i < cols.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(cols[i]);
            cell.setCellStyle(headerStyle);
        }

        // Data rows
        int rowNum = 1;
        for (Expense e : expenses) {
            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(e.getId());
            row.createCell(1).setCellValue(e.getDate().toString());
            row.createCell(2).setCellValue(e.getCategory());
            row.createCell(3).setCellValue(e.getDescription() != null ? e.getDescription() : "");
            row.createCell(4).setCellValue(e.getAmount());
            row.createCell(5).setCellValue(e.getBillImageUrl() != null ? "Yes" : "No");
        }

        // Auto-size columns
        for (int i = 0; i < cols.length; i++) {
            sheet.autoSizeColumn(i);
        }

        workbook.write(response.getOutputStream());
        workbook.close();
    }
}