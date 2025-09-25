package com.tambo.tambo_delivery_backend.services;

import com.tambo.tambo_delivery_backend.entities.Order;
import com.tambo.tambo_delivery_backend.entities.OrderItem;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.awt.Color;

@Service
public class BoletaPdfService {

    @Autowired
    private JavaMailSender mailSender;

    public byte[] generateBoletaPdf(Order order) {
        try {
            Document document = new Document();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, outputStream);

            document.open();

            // ======= ESTILOS =======
            Font titleFont = new Font(Font.HELVETICA, 20, Font.BOLD, new Color(40, 40, 40));
            Font subtitleFont = new Font(Font.HELVETICA, 12, Font.BOLD, new Color(60, 60, 60));
            Font normalFont = new Font(Font.HELVETICA, 11, Font.NORMAL, Color.DARK_GRAY);
            Font tableHeaderFont = new Font(Font.HELVETICA, 12, Font.BOLD, Color.WHITE);

            // ======= CABECERA =======
            Paragraph title = new Paragraph("🧾 Boleta de Venta - Tambo Delivery", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(15f);
            document.add(title);

            document.add(new Paragraph("Cliente: " + order.getUser().getUsername(), normalFont));
            document.add(new Paragraph("Fecha de Pedido: " + order.getOrderDate(), normalFont));
            document.add(new Paragraph("Método de Pago: " + order.getPaymentMethod(), normalFont));
            document.add(new Paragraph("Estado del Pedido: " + order.getOrderStatus(), normalFont));
            document.add(new Paragraph(" "));

            // ======= TABLA DE PRODUCTOS =======
            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);
            table.setWidths(new int[] { 5, 2, 3 });

            // Encabezados
            String[] headers = { "Producto", "Cantidad", "Subtotal" };
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, tableHeaderFont));
                cell.setBackgroundColor(new Color(100, 149, 237)); // Azul claro
                cell.setPadding(6);
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(cell);
            }

            // Filas
            for (OrderItem item : order.getOrderItemList()) {
                BigDecimal price = BigDecimal.valueOf(item.getItemPrice());
                BigDecimal quantity = BigDecimal.valueOf(item.getQuantity());
                BigDecimal total = price.multiply(quantity);

                table.addCell(new PdfPCell(new Phrase(item.getProduct().getName(), normalFont)));
                table.addCell(new PdfPCell(new Phrase(String.valueOf(item.getQuantity()), normalFont)));
                table.addCell(new PdfPCell(new Phrase("S/ " + total.toString(), normalFont)));
            }

            document.add(table);

            // ======= TOTAL =======
            Paragraph total = new Paragraph("Total a pagar: S/ " + order.getTotalAmount(), subtitleFont);
            total.setAlignment(Element.ALIGN_RIGHT);
            total.setSpacingBefore(10f);
            document.add(total);

            // ======= PIE DE PÁGINA =======
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Gracias por su compra en Tambo Delivery 🛒", normalFont));

            document.close();
            return outputStream.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al generar el PDF");
        }
    }

    public void enviarBoletaPorCorreo(Order order) {
        try {
            byte[] pdfBytes = generateBoletaPdf(order);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(order.getUser().getEmail());
            helper.setSubject("Tu boleta de compra - Tambo");
            helper.setText("Gracias por tu compra. Adjuntamos la boleta en PDF.");

            helper.addAttachment("boleta_tambo.pdf", new ByteArrayResource(pdfBytes));

            mailSender.send(message);
            System.out.println("Boleta enviada al correo: " + order.getUser().getEmail());
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error al enviar boleta por correo: " + e.getMessage());
        }
    }
}
