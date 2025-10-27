package com.tambo.tambo_delivery_backend.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tambo.tambo_delivery_backend.services.ExportService;
import com.tambo.tambo_delivery_backend.services.OrderService;
import com.tambo.tambo_delivery_backend.services.ProductService;
import com.tambo.tambo_delivery_backend.dto.response.ProductDTO;
import com.tambo.tambo_delivery_backend.entities.Order;

@RestController
@RequestMapping("/api/export")
public class ExportController {

    @Autowired
    private ExportService exportService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @GetMapping("/all-orders/excel")
    public ResponseEntity<byte[]> exportAllOrdersExcel() throws Exception {
        List<Order> orders = orderService.getAllOrdersByDateDesc();
        byte[] excelBytes = exportService.exportAllOrdersToExcel(orders);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=orders.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelBytes);
    }

    @GetMapping("/all-products/excel")
    public ResponseEntity<byte[]> exportAllProductsExcel() throws Exception {
        List<ProductDTO> product = productService.getAllProducts(null, null, null, null, null, null, null);
        byte[] excelBytes = exportService.exportAllProductsExcel(product);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=product.xlsx")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(excelBytes);
    }

}
