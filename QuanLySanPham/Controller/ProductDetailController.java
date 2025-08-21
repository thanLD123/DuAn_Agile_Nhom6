package com.example.demo.controllers.admin;

import com.example.demo.entities.Color;
import com.example.demo.entities.Image;
import com.example.demo.entities.Product;
import com.example.demo.entities.Size;
import com.example.demo.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;

@Controller
public class ProductDetailController {
    private Product productInLine;
    private final List<Image> imageList = new ArrayList<>();
    private long idImage;

    @Autowired
    private ProductDetailService productDetailService;

    @Autowired
    private ProductService productService;

    @Autowired
    private SizeService sizeService;
    @Autowired
    private ColorService colorService;

    @Autowired
    private ImageService imageService;

    @GetMapping("/admin/chi-tiet-san-pham/{code}")
    public String getProductDetailPage(@PathVariable String code, Model model) {
        Product product = productService.getProductByCode(code);

        if (product != null) {
            model.addAttribute("product", product);
            model.addAttribute("productDetails", product.getProductDetails());
            return "admin/product-detail";
        } else {
            // Log the issue
            System.err.println("Product not found for code: " + code);
            model.addAttribute("error", "Product not found");
            return "error/404"; // Handle error page more gracefully
        }
    }

    @ModelAttribute("listSize")
    public List<Size> getSize() {
        return sizeService.getAll();
    }

    @ModelAttribute("listColor")
    public List<Color> getColor() {
        return colorService.findAll();
    }
}
