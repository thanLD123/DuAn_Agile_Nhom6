package com.example.demo.controllers.admin;

import com.example.demo.dto.product.ProductDto;
import com.example.demo.entities.Product;
import com.example.demo.services.BillService;
import com.example.demo.services.CustomerService;
import com.example.demo.services.ProductService;
import com.example.demo.services.serviceImpl.TempProductQuantityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class BuyAtTheCounterController {
    @Autowired
    ProductService productService;
    @Autowired
    BillService billService;
    @Autowired
    TempProductQuantityService tempProductQuantityService;

    @Autowired
    CustomerService customerService;

//    @GetMapping("/admin/pos")
//    public String getIndex(Model model) {
//        tempProductQuantityService.reset();
//        Pageable pageable1 = PageRequest.of(0, 2);
//        Page<Product> productPage = productService.getAllProduct(pageable1);
//        model.addAttribute("products", productPage);
//        List<Long> ids=billService.findAllInStoreInvoiceId();//List id instore invoice
//        model.addAttribute("inStoreInvoiceId",ids);
//        model.addAttribute("inStoreInvoice",billService.findAllInStoreInvoice(ids));
//        return "admin/BuyAtTheCounter";
//    }
@GetMapping("/admin/pos")
public String getIndex(Model model) {
    // Reset tạm thời sản phẩm
    tempProductQuantityService.reset();

    // Lấy danh sách sản phẩm với phân trang
    Pageable pageable = PageRequest.of(0, 10); // Lấy 10 sản phẩm
    Page<ProductDto> productPage = productService.getAllProductApi(pageable); // Giả sử bạn có phương thức này
    model.addAttribute("products", productPage);

    // Lấy danh sách hóa đơn trong cửa hàng
    List<Long> ids = billService.findAllInStoreInvoiceId(); // List id hóa đơn trong cửa hàng
    model.addAttribute("inStoreInvoiceId", ids);
    model.addAttribute("inStoreInvoice", billService.findAllInStoreInvoice(ids));

    return "admin/BuyAtTheCounter"; // Trả về view
}
}

