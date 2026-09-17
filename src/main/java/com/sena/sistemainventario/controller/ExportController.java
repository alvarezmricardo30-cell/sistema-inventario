package com.sena.sistemainventario.controller;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;

import com.sena.sistemainventario.model.Producto;
import com.sena.sistemainventario.service.ProductoService;

@RestController
public class ExportController {

    private final ProductoService productoService;

    public ExportController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping("/exportar-pdf")
    public ResponseEntity<byte[]> exportarPDF() {
        try {
            List<Producto> productos = productoService.listarProductos();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();

            try (PdfWriter writer = new PdfWriter(stream);
                 PdfDocument pdf = new PdfDocument(writer);
                 Document document = new Document(pdf)) {

                String fechaHora = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"));

                document.add(new Paragraph("REPORTE DE PRODUCTOS"));
                document.add(new Paragraph("Sistema de Inventario SENA"));
                document.add(new Paragraph(" "));
                document.add(new Paragraph("Fecha y Hora: " + fechaHora));
                document.add(new Paragraph("Total de productos: " + productos.size()));

                double totalInventario = 0;
                int totalCantidad = 0;
                for (Producto p : productos) {
                    totalInventario += p.getPrecio() * p.getCantidad();
                    totalCantidad += p.getCantidad();
                }
                document.add(new Paragraph("Valor total del inventario: $" + String.format("%,.0f", totalInventario)));
                document.add(new Paragraph("Cantidad total de unidades: " + totalCantidad));
                document.add(new Paragraph(" "));

                Table tabla = new Table(7).useAllAvailableWidth();
                tabla.addHeaderCell(new Cell().add(new Paragraph("Codigo")));
                tabla.addHeaderCell(new Cell().add(new Paragraph("Producto")));
                tabla.addHeaderCell(new Cell().add(new Paragraph("Categoria")));
                tabla.addHeaderCell(new Cell().add(new Paragraph("Proveedor")));
                tabla.addHeaderCell(new Cell().add(new Paragraph("Precio")));
                tabla.addHeaderCell(new Cell().add(new Paragraph("Cantidad")));
                tabla.addHeaderCell(new Cell().add(new Paragraph("Valor Total")));

                for (Producto p : productos) {
                    tabla.addCell(new Cell().add(new Paragraph(p.getCodigo())));
                    tabla.addCell(new Cell().add(new Paragraph(p.getNombre())));
                    tabla.addCell(new Cell().add(new Paragraph(p.getCategoria())));
                    tabla.addCell(new Cell().add(new Paragraph(p.getProveedor() != null ? p.getProveedor() : "N/A")));
                    tabla.addCell(new Cell().add(new Paragraph("$" + String.format("%,.0f", p.getPrecio()))));
                    tabla.addCell(new Cell().add(new Paragraph(String.valueOf(p.getCantidad()))));
                    tabla.addCell(new Cell().add(new Paragraph("$" + String.format("%,.0f", p.getPrecio() * p.getCantidad()))));
                }

                document.add(tabla);
                document.add(new Paragraph(" "));
                document.add(new Paragraph("Documento generado automaticamente por el Sistema de Inventario SENA"));
            }

            byte[] pdfBytes = stream.toByteArray();
            String nombreArchivo = "reporte_productos_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf";

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + nombreArchivo)
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(pdfBytes.length)
                    .body(pdfBytes);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/exportar-excel")
    public ResponseEntity<byte[]> exportarExcel() {
        try {
            List<Producto> productos = productoService.listarProductos();

            try (Workbook workbook = new XSSFWorkbook();
                 ByteArrayOutputStream stream = new ByteArrayOutputStream()) {

                Sheet sheet = workbook.createSheet("Productos");

                CellStyle headerStyle = workbook.createCellStyle();
                Font headerFont = workbook.createFont();
                headerFont.setBold(true);
                headerStyle.setFont(headerFont);

                Row headerRow = sheet.createRow(0);
                String[] headers = {"Codigo", "Producto", "Categoria", "Proveedor", "Precio", "Cantidad", "Valor Total"};
                for (int i = 0; i < headers.length; i++) {
                    org.apache.poi.ss.usermodel.Cell cell = headerRow.createCell(i);
                    cell.setCellValue(headers[i]);
                    cell.setCellStyle(headerStyle);
                }

                int rowNum = 1;
                double totalInventario = 0;
                for (Producto p : productos) {
                    Row row = sheet.createRow(rowNum++);
                    double valorTotal = p.getPrecio() * p.getCantidad();
                    totalInventario += valorTotal;
                    row.createCell(0).setCellValue(p.getCodigo());
                    row.createCell(1).setCellValue(p.getNombre());
                    row.createCell(2).setCellValue(p.getCategoria());
                    row.createCell(3).setCellValue(p.getProveedor() != null ? p.getProveedor() : "N/A");
                    row.createCell(4).setCellValue(p.getPrecio());
                    row.createCell(5).setCellValue(p.getCantidad());
                    row.createCell(6).setCellValue(valorTotal);
                }

                Row totalRow = sheet.createRow(rowNum + 1);
                totalRow.createCell(3).setCellValue("TOTAL:");
                totalRow.createCell(3).setCellStyle(headerStyle);
                totalRow.createCell(6).setCellValue(totalInventario);
                totalRow.createCell(6).setCellStyle(headerStyle);

                Row fechaRow = sheet.createRow(rowNum + 3);
                fechaRow.createCell(0).setCellValue("Fecha y Hora:");
                fechaRow.createCell(1).setCellValue(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));

                for (int i = 0; i < headers.length; i++) {
                    sheet.autoSizeColumn(i);
                }

                workbook.write(stream);
                byte[] excelBytes = stream.toByteArray();

                String nombreArchivo = "productos_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".xlsx";

                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + nombreArchivo)
                        .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                        .contentLength(excelBytes.length)
                        .body(excelBytes);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}