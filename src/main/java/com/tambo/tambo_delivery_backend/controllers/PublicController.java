package com.tambo.tambo_delivery_backend.controllers;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tambo.tambo_delivery_backend.auth.dto.response.ResponseDto;
import com.tambo.tambo_delivery_backend.dto.response.CategoryDTO;
import com.tambo.tambo_delivery_backend.dto.response.ProductDTO;
import com.tambo.tambo_delivery_backend.dto.response.ProductSectionDTO;
import com.tambo.tambo_delivery_backend.services.AppConfigService;
import com.tambo.tambo_delivery_backend.services.CategoryService;
import com.tambo.tambo_delivery_backend.services.ProductService;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/api/public")
@CrossOrigin
public class PublicController {

    @Autowired
    private ProductService productService;

    @Autowired
    private AppConfigService configService;

    @Autowired
    private CategoryService categoryService;

    // ------------------------------ PRODUCT -----------------------------

    // Obtener todos los productos por filtros
    @GetMapping("/product")
    public ResponseEntity<?> getAllProducts(
            @RequestParam(required = false) UUID categoryId,
            @RequestParam(required = false) UUID typeId,
            @RequestParam(required = false) String slug,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Boolean newArrival,
            HttpServletResponse response) {

        try {
            List<ProductDTO> productList = new ArrayList<>();

            if (StringUtils.isNotBlank(slug)) {
                ProductDTO productDto = productService.getProductBySlug(slug);
                productList.add(productDto);
            } else {
                productList = productService.getAllProducts(categoryId, typeId, name, minPrice, maxPrice, true,
                        newArrival);
            }
            return new ResponseEntity<>(productList, HttpStatus.OK);

        } catch (RuntimeException e) {
            ResponseDto res = ResponseDto.builder()
                    .message("Error al obtener los productos: " + e.getMessage())
                    .build();
            return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
        }

    }

    // ------------------------------ PRODUCT SECTIONS ----------------------------

    // Obtener las secciones de categorias
    @GetMapping("/product-sections")
    public ResponseEntity<?> getAllProductSections() {

        try {
            List<ProductSectionDTO> prod = configService.getProductSections();
            return new ResponseEntity<>(prod, HttpStatus.OK);
        } catch (RuntimeException e) {
            ResponseDto res = ResponseDto.builder()
                    .message("Error al obtener a las secciones de los productos: " + e.getMessage())
                    .build();
            return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
        }

    }

    // ------------------------------ CATEGORIES ----------------------------

    // Obtener todas las categorías (público)
    @GetMapping("/category/get-all")
    public ResponseEntity<?> getAllCategories() {
        try {
            List<CategoryDTO> categories = categoryService.getAllCategories();
            return new ResponseEntity<>(categories, HttpStatus.OK);
        } catch (RuntimeException e) {
            ResponseDto res = ResponseDto.builder()
                    .message("Error al obtener las categorías: " + e.getMessage())
                    .build();
            return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
        }
    }

    // Obtener productos por categoría con límite (para home page)
    @GetMapping("/product/by-category")
    public ResponseEntity<?> getProductsByCategory(
            @RequestParam UUID categoryId,
            @RequestParam(defaultValue = "6") Integer limit) {
        
        try {
            List<ProductDTO> products = productService.getAllProducts(
                categoryId, null, null, null, null, true, null
            );
            
            // Limitar la cantidad de productos si se especifica
            if (limit != null && limit > 0 && products.size() > limit) {
                products = products.subList(0, limit);
            }
            
            return new ResponseEntity<>(products, HttpStatus.OK);
        } catch (RuntimeException e) {
            ResponseDto res = ResponseDto.builder()
                    .message("Error al obtener productos por categoría: " + e.getMessage())
                    .build();
            return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
        }
    }
}