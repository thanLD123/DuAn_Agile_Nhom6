package com.example.demo.controllers.admin;

import com.example.demo.dto.ProductDiscount.ProductDiscountCreateDto;
import com.example.demo.dto.ProductDiscount.ProductDiscountDto;
import com.example.demo.entities.Color;
import com.example.demo.entities.Product;
import com.example.demo.entities.ProductDiscount;
import com.example.demo.entities.Size;
import com.example.demo.exceptions.NotFoundException;
import com.example.demo.repositories.*;
import com.example.demo.services.ProductDiscountService;
import com.example.demo.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class ProductDiscountController {
    private final ProductService productService;
    private final ProductDiscountService productDiscountService;
    private final ProductDetailRepository productDetailRepository;
    private final ProductDiscountRepository productDiscountRepository;
    private final ColorRepository colorRepository; // Khai báo ColorRepository
    private final SizeRepository sizeRepository; // Khai báo SizeRepository
    private final ProductRepository productRepository; // Khai báo ProductRepository

    // Constructor injection
    @Autowired
    public ProductDiscountController(ProductService productService,
                                     ProductRepository productRepository,
                                     ProductDiscountService productDiscountService,
                                     ProductDetailRepository productDetailRepository,
                                     ProductDiscountRepository productDiscountRepository,
                                     ColorRepository colorRepository,
                                     SizeRepository sizeRepository) { // Thêm ColorRepository vào constructor
        this.productService = productService;
        this.productDiscountService = productDiscountService;
        this.productDetailRepository = productDetailRepository;
        this.productDiscountRepository = productDiscountRepository;
        this.colorRepository = colorRepository; // Gán biến colorRepository
        this.productRepository = productRepository; // Gán biến productRepository
        this.sizeRepository = sizeRepository;
    }

    @GetMapping("/admin-only/product-discount")
    public String viewProductDiscountPage(Model model) {
        List<ProductDiscount> productDiscountList = productDiscountRepository.findAll();
        model.addAttribute("productDiscounts", productDiscountList);
        return "/admin/product-discount";
    }

    @GetMapping("/admin-only/product-discount-create")
    public String viewProductDiscountCreatePage(Model model, @RequestParam(required = false, name = "color") String color,
                                                @RequestParam(required = false, name = "size") String size) {
        List<Product> products = productRepository.findAll();
        // Lấy danh sách các màu sắc và kích cỡ từ repository
        List<Color> colors = colorRepository.findAll();
        List<Size> sizes = sizeRepository.findAll();

        // Thêm danh sách vào model để hiển thị trong giao diện
        model.addAttribute("colors", colors);
        model.addAttribute("sizes", sizes);

        // Thêm giá trị lọc hiện tại vào model để hiển thị lại trên giao diện
        model.addAttribute("selectedColor", color);
        model.addAttribute("selectedSize", size);


        // Thêm danh sách vào model để hiển thị trong giao diện
        model.addAttribute("colors", colors);
        model.addAttribute("sizes", sizes);

        // Thêm giá trị lọc hiện tại vào model để hiển thị lại trên giao diện
        model.addAttribute("selectedColor", color);
        model.addAttribute("selectedSize", size);

        Color colorEntity = null;
        Size sizeEntity = null;

        if (color != null && !color.isEmpty()) {
            colorEntity = colorRepository.findByName(color)
                    .orElseThrow(() -> new NotFoundException("Color not found"));
        }

        if (size != null && !size.isEmpty()) {
            sizeEntity = sizeRepository.findByName(size)
                    .orElseThrow(() -> new NotFoundException("Size not found"));
        }

        List<ProductDiscount> productDiscountList = productDiscountService.getAllProductDiscount(colorEntity, sizeEntity);
        model.addAttribute("products", productDiscountList);
        return "/admin/product-discount-create";
    }

    @ResponseBody
    @PostMapping("/api/private/product-discount/multiple")
    public List<ProductDiscountDto> createProductDiscountMultiple(@Valid @RequestBody ProductDiscountCreateDto productDiscountCreateDto) {
        return productDiscountService.createProductDiscountMultiple(productDiscountCreateDto);
    }

    @ResponseBody
    @PostMapping("/api/private/product-discount/{id}/status/{status}")
    public ProductDiscountDto updateProductDiscount(@Valid @PathVariable Long id, @PathVariable boolean status) {
        return productDiscountService.updateCloseProductDiscount(id, status);
    }
    @ResponseBody
    @GetMapping("/api/private/product-discount/check/{productDetailId}")
    public ResponseEntity<Map<String, Boolean>> checkDiscount(@PathVariable Long productDetailId) {
        Date now = new Date();
        boolean hasDiscount = productDiscountRepository.existsActiveDiscount(productDetailId, now);
        Map<String, Boolean> response = new HashMap<>();
        response.put("hasDiscount", hasDiscount);
        return ResponseEntity.ok(response);
    }
}
