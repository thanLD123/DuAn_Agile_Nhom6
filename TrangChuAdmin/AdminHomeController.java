package com.example.demo.controllers.admin;

import com.example.demo.dto.Bill.BillDtoInterface;
import com.example.demo.dto.product.ProductDto;
import com.example.demo.repositories.BillRepository;
import com.example.demo.services.AccountService;
import com.example.demo.services.BillService;
import com.example.demo.services.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminHomeController {
    private final BillService billService;
    private final ProductService productService;
    private final BillRepository billRepository;
    private final AccountService accountService;

    public AdminHomeController(BillService billService, ProductService productService, BillRepository billRepository, AccountService accountService) {
        this.billService = billService;
        this.productService = productService;
        this.billRepository = billRepository;
        this.accountService = accountService;
    }

    @GetMapping("/admin")
    public String viewAdminHome(Model model) {
        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "createDate"));
        Page<BillDtoInterface> billDtos = billService.findAll(pageable);

        Page<ProductDto> productDtos = productService.getAllProductApi(Pageable.ofSize(10));
        double totalRevenue = billRepository.calculateTotalRevenue();
        long totalBillWaiting = billRepository.getTotalBillStatusWaiting();
        long totalBillWaiting2 = billRepository.getTotalBillStatusWaiting2();

        // Truyền dữ liệu sang giao diện
        model.addAttribute("billList", billDtos.getContent());
        model.addAttribute("totalBillQuantity", totalBillWaiting2);
        model.addAttribute("totalProduct", productDtos.getTotalElements());
        model.addAttribute("revenue", totalRevenue);
        model.addAttribute("totalBillWaiting", totalBillWaiting);

        return "/admin/index";
    }

    @GetMapping("/admin/thong-ke-san-pham")
    public String viewStatisticProductPage(Model model) {

        return "/admin/thong-ke-san-pham";
    }
}
